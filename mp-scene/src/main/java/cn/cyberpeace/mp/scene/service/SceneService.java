package cn.cyberpeace.mp.scene.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scene.constant.SceneErrorCode;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.event.SceneElementEventTransition;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.core.event.base.EventTransition;
import cn.cyberpeace.mp.scene.core.event.dispatch.SceneHandlerProxyBuilder;
import cn.cyberpeace.mp.scene.core.exception.SceneNotExistException;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.SceneElementSnapshotMapper;
import cn.cyberpeace.mp.scene.service.taskpool.ElementTaskPool;
import cn.cyberpeace.mp.scene.service.taskpool.SceneTaskPool;
import cn.cyberpeace.mp.scene.util.ResourceClearHandler;
import cn.cyberpeace.mp.scene.util.SceneEventUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@Slf4j
@Service
public class SceneService {

    @Resource
    private SceneConfigService sceneConfigService;

    @Resource
    private SceneElementSnapshotMapper sceneElementSnapshotMapper;

    @Autowired
    private ResourceClearHandler resourceClearHandler;

    public ServiceResult<SceneDataBO> get(String id, boolean isAreaMode, String areaId, SceneContextBO context) {
        SceneHandler sceneHandler;
        try {
            sceneHandler = new SceneHandler(id, areaId, isAreaMode, context);
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(null, SceneErrorCode.SCENE_NOT_EXIST);
        }
        return new ServiceResult<>(sceneHandler.toData());
    }

    public ServiceResult<SceneDataBO> create(SceneDataBO sceneDataBO, SceneContextBO context, boolean partialStart, Runnable callback) {
        SceneHandler sceneHandler;
        try {
            sceneHandler = new SceneHandler(sceneDataBO, false, context);
            sceneHandler.setRealTimeUpdate(true);
            if (sceneHandler.getScene() != null) {
                sceneHandler.getScene().setCreateTime(null);
            }
            sceneHandler.reformTopo();
            sceneHandler.save();
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(null, SceneErrorCode.SCENE_NOT_EXIST);
        }

        SceneHandler sceneHandlerProxy = SceneHandlerProxyBuilder.build(sceneHandler);
        SceneTaskPool.getInstance().executeTask(() -> {
            var starTime = LocalDateTime.now();
            sceneHandler.prepare(partialStart);
            if (!sceneHandlerProxy.initiate(false)) {
                return;
            }
            sceneHandlerProxy.create();

            var endTime = LocalDateTime.now();
            var consumeSeconds = Duration.between(starTime, endTime).toSeconds();
            sceneHandler.getScene().setCreateConsumeSeconds(consumeSeconds);
            sceneHandler.updateScene();
            if (callback != null) {
                callback.run();
            }
        });

        return new ServiceResult<>(sceneHandler.toData());
    }

    public ServiceResult<SceneDataBO> createFromSceneConfig(SceneConfigDataBO sceneConfigDataBO,
                                                            SceneContextBO context) {
        return create(sceneConfigDataBO.exportToScene(), context, false, null);
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


            sceneHandler = SceneHandlerProxyBuilder.build(id, null, false, context, true);
            if (logic) {
                SceneTaskPool.getInstance().executeTask(() -> sceneHandler.logicDelete());
            } else {
                SceneTaskPool.getInstance().executeTask(() -> {
                    try {
                        sceneHandler.delete();
                    } finally {
                        resourceClearHandler.doCallback(id);
                    }
                });
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

    public ServiceResult<SceneDataBO> modify(String sceneId, SceneDataBO updatedSceneData, SceneContextBO context, Runnable runner) {
        SceneHandler originSceneHandler = new SceneHandler(sceneId, null, false, context);
        
        String newDisplayContext = updatedSceneData.getTopology().getScene().getDisplayContext();
        updatedSceneData.getTopology().setScene(originSceneHandler.getScene());
        updatedSceneData.getTopology().getScene().setDisplayContext(newDisplayContext);
        SceneHandler updatedSceneHandler = new SceneHandler(updatedSceneData, context);

        Queue<SceneElementBO> allElements = new ArrayDeque<>(updatedSceneHandler.getElementMap().values());
        while (!allElements.isEmpty()) {
            var element = allElements.poll();
            if (element != null) {
                var connectPorts = updatedSceneHandler.getElementConnectedPorts(element.getId());
                if (CollectionUtils.isNotEmpty(connectPorts)) {
                    Set<String> ips = new HashSet<>();
                    for (var p : connectPorts) {
                        if (StringUtils.isNotEmpty(p.getIp())) {
                            if (ips.contains(p.getIp())) {
                                throw new MpRuntimeException(SceneErrorCode.IP_DUPLICATE.format(p.getIp()));
                            } else {
                                ips.add(p.getIp());
                            }
                        }
                    }
                }
            }
        }

        updatedSceneHandler.setRealTimeUpdate(true);
        if (updatedSceneHandler.getScene() != null) {
            updatedSceneHandler.getScene().setCreateTime(null);
        }
        updatedSceneHandler.save();
        
        List<String> updateElementIds = new ArrayList<>();
        originSceneHandler.getElementMap().values().forEach(e -> {
            if (e.getEvent() == SceneModel.Event.MODIFY && e.getEventStatus() == EventStatus.ABNORMAL) {
                updateElementIds.add(e.getId());
            }
        });
        SceneTaskPool.getInstance().executeTask(() -> {
            originSceneHandler.getElementMap().values().forEach(e -> {
                if (e.getEvent() == SceneElementModel.Event.DEFAULT) {
                    updatedSceneHandler.addExcludeHandleNode(e.getId());
                    originSceneHandler.addExcludeHandleNode(e.getId());
                } else {
                    e.setEvent(SceneElementModel.Event.DEFAULT);
                    e.setEventStatus(EventStatus.INITIAL);
                }
            });

            for (var e : updatedSceneHandler.getElementMap().values()) {
                if (updatedSceneHandler.isHandleNode(e.getId())) {
                    e.setEvent(SceneElementModel.Event.DEFAULT);
                    e.setEventStatus(EventStatus.INITIAL);
                }
            }
            SceneHandlerProxyBuilder.build(updatedSceneHandler);
            SceneHandler proxy = SceneHandlerProxyBuilder.build(updatedSceneHandler);
            proxy.modify(originSceneHandler, updateElementIds);
            
            runner.run();
        });
        return new ServiceResult<>(updatedSceneHandler.toData());
    }

    public ServiceResult<Boolean> pause(String id, SceneContextBO context) {


        if (SceneEventUtils.hasProcessingScene(SceneModel.Event.UNPAUSE, id)) {
            throw new MpRuntimeException(SceneErrorCode.ENTER_EVENT_FAILED);
        }
        SceneHandler sceneHandler = SceneHandlerProxyBuilder.build(id, null, false, context, true);
        if (sceneHandler.getScene().getEvent() == SceneModel.Event.PAUSE && (sceneHandler.getScene().getEventStatus() == EventStatus.IN_PROGRESS || sceneHandler.getScene().getEventStatus() == EventStatus.START)) {
            throw new MpRuntimeException(SceneErrorCode.ENTER_EVENT_FAILED);
        } else if (sceneHandler.getScene().getEvent() == SceneModel.Event.PAUSE && sceneHandler.getScene().getEventStatus() == EventStatus.OVER) {
            return new ServiceResult<>(true);
        }
        SceneEventUtils.addProcessingScene(SceneModel.Event.PAUSE, id);
        SceneTaskPool.getInstance().executeTask(() -> {
            try {
                sceneHandler.pause();
            } finally {
                SceneEventUtils.removeProcessingScene(SceneModel.Event.PAUSE, id);
            }
        });
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> unpause(String id, SceneContextBO context) {


        if (SceneEventUtils.hasProcessingScene(SceneModel.Event.PAUSE, id)) {
            throw new MpRuntimeException(SceneErrorCode.ENTER_EVENT_FAILED);
        }
        SceneHandler sceneHandler = SceneHandlerProxyBuilder.build(id, null, false, context, true);
        if (sceneHandler.getScene().getEvent() == SceneModel.Event.UNPAUSE && (sceneHandler.getScene().getEventStatus() == EventStatus.IN_PROGRESS || sceneHandler.getScene().getEventStatus() == EventStatus.START)) {
            throw new MpRuntimeException(SceneErrorCode.ENTER_EVENT_FAILED);
        } else if (sceneHandler.getScene().getEvent() == SceneModel.Event.UNPAUSE && sceneHandler.getScene().getEventStatus() == EventStatus.OVER) {
            return new ServiceResult<>(true);
        }
        SceneEventUtils.addProcessingScene(SceneModel.Event.UNPAUSE, id);
        SceneTaskPool.getInstance().executeTask(() -> {
            try {
                sceneHandler.unpause();
            } finally {
                SceneEventUtils.removeProcessingScene(SceneModel.Event.UNPAUSE, id);
            }
        });
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> suspend(String id, SceneContextBO context) {


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


        SceneHandler sceneHandler = SceneHandlerProxyBuilder.build(id, null, false, context, true);
        SceneTaskPool.getInstance().executeTask(() -> sceneHandler.resume());
        return new ServiceResult<>(true);
    }

    
    public ServiceResult<Boolean> startElement(String id, String nodeId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        
        ElementTaskPool.getInstance().executeTask(() -> sceneHandler.startElement(nodeId));
        return new ServiceResult<>(true);
    }

    
    public ServiceResult<Boolean> stopElement(String id, String nodeId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        
        ElementTaskPool.getInstance().executeTask(() -> sceneHandler.stopElement(nodeId));
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

    public ServiceResult<Boolean> createElement(String id, String nodeId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        ElementTaskPool.getInstance().executeTask(() -> {
            sceneHandler.initiateElement(nodeId);
            sceneHandler.createElement(nodeId);
        });
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> recreateElement(String id, String nodeId,String image, SceneContextBO context,
                                                  Runnable callback) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        ElementTaskPool.getInstance().executeTask(() -> {
            sceneHandler.recreateElement(nodeId,image);
            callback.run();
        });
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> deleteElement(String id, String nodeId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        ElementTaskPool.getInstance().executeTask(() -> sceneHandler.deleteElement(nodeId));
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> modifyElement(String id, String nodeId, SceneDataBO newDataBO, SceneContextBO context) {
        SceneHandler originHandler = new SceneHandler(id, null, false, context);
        newDataBO.getTopology().setScene(originHandler.getScene());
        SceneHandler updateHandler = new SceneHandler(newDataBO, context);

        var originElement = originHandler.getElement(nodeId);
        var newElement = updateHandler.getElement(nodeId);
        newElement.setEid(originElement.getEid());
        newElement.setEvent(originElement.getEvent());
        newElement.setEventStatus(originElement.getEventStatus());
        newElement.setProcess(originElement.getProcess());
        newElement.setProcessMessage(originElement.getProcessMessage());
        newElement.setProcessStatus(originElement.getProcessStatus());
        if (CollectionUtils.isNotEmpty(newElement.getPorts())) {
            for (var newPort : newElement.getPorts()) {
                var originPort = originHandler.getPort(newPort.getId());
                if (originPort != null) {
                    newPort.setEid(originPort.getEid());
                }
            }
        }

        ElementTaskPool.getInstance().executeTask(() -> {
            updateHandler.modifyElement(nodeId, originHandler);
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
