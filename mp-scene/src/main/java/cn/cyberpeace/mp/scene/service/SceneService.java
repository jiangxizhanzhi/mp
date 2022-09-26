package cn.cyberpeace.mp.scene.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scene.constant.SceneErrorCode;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import cn.cyberpeace.mp.scene.core.event.SceneElementEventTransition;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.core.event.base.EventTransition;
import cn.cyberpeace.mp.scene.core.event.dispatch.SceneHandlerProxyBuilder;
import cn.cyberpeace.mp.scene.core.exception.SceneNotExistException;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneElementSnapshotModel;
import cn.cyberpeace.mp.scene.repository.SceneElementSnapshotMapper;
import cn.cyberpeace.mp.scene.service.taskpool.ElementTaskPool;
import cn.cyberpeace.mp.scene.service.taskpool.SceneTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class SceneService {

    @Resource
    private SceneConfigService sceneConfigService;

    @Resource
    private SceneElementSnapshotMapper sceneElementSnapshotMapper;

    public ServiceResult<SceneDataBO> get(String id, boolean isAreaMode, String areaId, SceneContextBO context) {
        SceneHandler sceneHandler;
        try {
            sceneHandler = new SceneHandler(id, areaId, isAreaMode, context);
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(null, SceneErrorCode.SCENE_NOT_EXIST);
        }
        return new ServiceResult<>(sceneHandler.toData());
    }

    public ServiceResult<SceneDataBO> create(SceneDataBO sceneDataBO, SceneContextBO context) {
        SceneHandler sceneHandler;
        try {
            sceneHandler = new SceneHandler(sceneDataBO, false, context);
            sceneHandler.setRealTimeUpdate(true);
            if (sceneHandler.getScene() != null) {
                sceneHandler.getScene().setCreateTime(null);
            }
//            sceneHandler.reformTopo();
            sceneHandler.save();
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(null, SceneErrorCode.SCENE_NOT_EXIST);
        }

        SceneHandler sceneHandlerProxy = SceneHandlerProxyBuilder.build(sceneHandler);
        SceneTaskPool.getInstance().executeTask(() -> {
            var starTime = LocalDateTime.now();
            if (!sceneHandlerProxy.initiate(false)) {
                return;
            }
            sceneHandlerProxy.create();

            var endTime = LocalDateTime.now();
            var consumeSeconds = Duration.between(starTime, endTime).toSeconds();
            sceneHandler.getScene().setCreateConsumeSeconds(consumeSeconds);
            sceneHandler.updateScene();
        });

        return new ServiceResult<>(sceneHandler.toData());
    }

    public ServiceResult<SceneDataBO> update(SceneDataBO sceneDataBO, SceneContextBO context) {
        SceneHandler originSceneHandler = new SceneHandler(sceneDataBO.getTopology().getScene().getEid(), sceneDataBO.getTopology().getAreaId(), context);
        SceneHandler sceneHandler;
        try {
            sceneHandler = new SceneHandler(sceneDataBO, false, context);
            sceneHandler.setRealTimeUpdate(true);
            if (sceneHandler.getScene() != null) {
                sceneHandler.getScene().setCreateTime(null);
            }
//            sceneHandler.reformTopo();
            sceneHandler.save();
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(null, SceneErrorCode.SCENE_NOT_EXIST);
        }

        SceneTaskPool.getInstance().executeTask(() -> {
            SceneHandler sceneHandler2 = new SceneHandler(sceneHandler.getSceneId(), null, context);
            SceneHandler sceneHandlerProxy = SceneHandlerProxyBuilder.build(sceneHandler2);
            var starTime = LocalDateTime.now();
            sceneHandler2.getElementMap().values().forEach(e -> {
                e.setEvent(SceneElementModel.Event.DEFAULT);
                e.setEventStatus(EventStatus.INITIAL);
            });
            sceneHandlerProxy.initiate1(false);

            var endTime = LocalDateTime.now();
            var consumeSeconds = Duration.between(starTime, endTime).toSeconds();
            sceneHandler2.getScene().setCreateConsumeSeconds(consumeSeconds);
            sceneHandler2.updateScene();

            var diff = sceneHandler.getDiffElements();
            if (CollectionUtils.isNotEmpty(diff.getRemovingTargets())) {
                diff.getRemovingTargets().forEach(e -> {
                    originSceneHandler.deleteElements(e.getNodeId());
                });
            }
        });

        return new ServiceResult<>(sceneHandler.toData());
    }

    public ServiceResult<SceneDataBO> createFromSceneConfig(SceneConfigDataBO sceneConfigDataBO,
                                                            SceneContextBO context) {
        return create(sceneConfigDataBO.exportToScene(), context);
    }

    public ServiceResult<SceneDataBO> createFromSceneConfig(String sceneConfigId, SceneContextBO context) {
        var sceneConfigDataBO = sceneConfigService.get(sceneConfigId, false, null, null)
                .getSource();

        if (sceneConfigDataBO == null) {
            return new ServiceResult<>(null, SceneErrorCode.SCENE_NOT_EXIST);
        }

        return createFromSceneConfig(sceneConfigDataBO, context);
    }

    public ServiceResult<Boolean> delete(String id, boolean logic, SceneContextBO context) {
        SceneHandler sceneHandler;
        try {
//            sceneHandler = new SceneHandler(id, null, false, context);
//            sceneHandler.setRealTimeUpdate(true);
            sceneHandler = SceneHandlerProxyBuilder.build(id, null, false, context, true);
            if (logic) {
                SceneTaskPool.getInstance().executeTask(() -> sceneHandler.logicDelete());
            } else {
                SceneTaskPool.getInstance().executeTask(() -> sceneHandler.delete());
            }
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(false, SceneErrorCode.SCENE_NOT_EXIST);
        }

        return new ServiceResult<>(true);
    }

    public ServiceResult<Integer> bulkDelete(String[] ids, boolean logic, SceneContextBO context) {
        var result = 0;
        for (String id : ids) {
            if (Boolean.TRUE.equals(delete(id, logic, context).getSource())) {
                result += 1;
            }
        }

        if (result < ids.length) {
            log.error("Service bulk delete scene configs {} with some failed", Arrays.toString(ids));
            return new ServiceResult<>(result, BaseErrorCode.DATA_PARTIAL_SUCCESS);
        } else {
            return new ServiceResult<>(result);
        }
    }

    public ServiceResult<Boolean> pause(String id, SceneContextBO context) {
//        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
//        sceneHandler.setRealTimeUpdate(true);
        SceneHandler sceneHandler = SceneHandlerProxyBuilder.build(id, null, false, context, true);
        sceneHandler.getElementMap().values().forEach(e -> {
            var validEvents = SceneElementEventTransition.SCENE_ELEMENT_EVENT_TRANSITION.getOrDefault(
                    EventTransition.genKey(e.getEvent(), e.getEventStatus()), new ArrayList<>()
            );
            if (!validEvents.contains(SceneElementModel.Event.PAUSE)) {
                throw new MpRuntimeException(SceneErrorCode.SCENE_DOES_NOT_ALLOW_OPERATION.format(e.getProcessMessage()));
            }
        });
        SceneTaskPool.getInstance().executeTask(() -> sceneHandler.pause());
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> unpause(String id, SceneContextBO context) {
//        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
//        sceneHandler.setRealTimeUpdate(true);
        SceneHandler sceneHandler = SceneHandlerProxyBuilder.build(id, null, false, context, true);
        SceneTaskPool.getInstance().executeTask(() -> sceneHandler.unpause());
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> suspend(String id, SceneContextBO context) {
//        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
//        sceneHandler.setRealTimeUpdate(true);
        SceneHandler sceneHandler = SceneHandlerProxyBuilder.build(id, null, false, context, true);
        sceneHandler.getElementMap().values().forEach(e -> {
            var validEvents = SceneElementEventTransition.SCENE_ELEMENT_EVENT_TRANSITION.getOrDefault(
                    EventTransition.genKey(e.getEvent(), e.getEventStatus()), new ArrayList<>()
            );
            if (!validEvents.contains(SceneElementModel.Event.SUSPEND)) {
                throw new MpRuntimeException(SceneErrorCode.SCENE_DOES_NOT_ALLOW_OPERATION.format(e.getProcessMessage()));
            }
        });
        SceneTaskPool.getInstance().executeTask(() -> sceneHandler.suspend());
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> resume(String id, SceneContextBO context) {
//        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
//        sceneHandler.setRealTimeUpdate(true);
        SceneHandler sceneHandler = SceneHandlerProxyBuilder.build(id, null, false, context, true);
        SceneTaskPool.getInstance().executeTask(() -> sceneHandler.resume());
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> pauseElement(String id, String nodeId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        ElementTaskPool.getInstance().executeTask(() -> sceneHandler.pauseElement(nodeId));
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> unpauseElement(String id, String nodeId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        ElementTaskPool.getInstance().executeTask(() -> sceneHandler.unpauseElement(nodeId));
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> restartElement(String id, String nodeId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        ElementTaskPool.getInstance().executeTask(() -> sceneHandler.restartElement(nodeId));
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> recreateElement(String id, String nodeId, SceneContextBO context, Runnable callback) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        ElementTaskPool.getInstance().executeTask(() -> {
            sceneHandler.recreateElement(nodeId);
            callback.run();
        });
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> suspendElement(String id, String nodeId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        ElementTaskPool.getInstance().executeTask(() -> sceneHandler.suspendElement(nodeId));
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> resumeElement(String id, String nodeId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        ElementTaskPool.getInstance().executeTask(() -> sceneHandler.resumeElement(nodeId));
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> snapshotElementSync(String id, String nodeId, String snapshotId, String snapshotName, String description, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        sceneHandler.snapshotElement(nodeId, snapshotId, snapshotName, description);
        return new ServiceResult<>(true);
    }
}
