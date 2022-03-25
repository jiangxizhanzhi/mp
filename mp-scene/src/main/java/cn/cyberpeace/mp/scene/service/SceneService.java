package cn.cyberpeace.mp.scene.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scene.constant.SceneErrorCode;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import cn.cyberpeace.mp.scene.core.exception.SceneNotExistException;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.service.taskpool.ElementTaskPool;
import cn.cyberpeace.mp.scene.service.taskpool.SceneTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Service
public class SceneService {

    private SceneConfigService sceneConfigService;

    @Autowired
    public SceneService(SceneConfigService sceneConfigService) {
        this.sceneConfigService = sceneConfigService;
    }

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
            sceneHandler.save();
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(null, SceneErrorCode.SCENE_NOT_EXIST);
        }

        SceneTaskPool.getInstance().executeTask(() -> {
            var starTime = LocalDateTime.now();
            if (!sceneHandler.initiate(false)) {
                return;
            }
            sceneHandler.create();

            var endTime = LocalDateTime.now();
            var consumeSeconds = Duration.between(starTime, endTime).toSeconds();
            sceneHandler.getScene().setCreateConsumeSeconds(consumeSeconds);
            sceneHandler.updateScene();
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
            sceneHandler = new SceneHandler(id, null, false, context);
            sceneHandler.setRealTimeUpdate(true);
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
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        SceneTaskPool.getInstance().executeTask(() -> sceneHandler.pause());
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> unpause(String id, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        SceneTaskPool.getInstance().executeTask(() -> sceneHandler.unpause());
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> suspend(String id, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        SceneTaskPool.getInstance().executeTask(() -> sceneHandler.suspend());
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> resume(String id, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
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

    public ServiceResult<Boolean> recreateElement(String id, String nodeId, SceneContextBO context) {
        SceneHandler sceneHandler = new SceneHandler(id, null, false, context);
        sceneHandler.setRealTimeUpdate(true);
        ElementTaskPool.getInstance().executeTask(() -> sceneHandler.recreateElement(nodeId));
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

}
