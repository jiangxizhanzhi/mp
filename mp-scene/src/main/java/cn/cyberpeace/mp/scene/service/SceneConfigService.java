package cn.cyberpeace.mp.scene.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scene.constant.SceneErrorCode;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigContextBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.exception.SceneNotExistException;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class SceneConfigService {

    public ServiceResult<SceneConfigDataBO> get(String id, boolean isAreaMode, String areaId,
                                                SceneConfigContextBO context) {
        SceneConfigHandler sceneConfigHandler;
        try {
            sceneConfigHandler = new SceneConfigHandler(id, areaId, isAreaMode, context);
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(null, SceneErrorCode.SCENE_NOT_EXIST);
        }
        return new ServiceResult<>(sceneConfigHandler.toData());
    }

    public ServiceResult<SceneConfigDataBO> sync(SceneConfigDataBO sceneConfigDataBO, boolean isAreaMode,
                                                 SceneConfigContextBO context) {
        SceneConfigHandler sceneConfigHandler;
        try {
            sceneConfigHandler = new SceneConfigHandler(sceneConfigDataBO, isAreaMode, context);
            sceneConfigHandler.validate();
            if (sceneConfigHandler.getScene() != null) {
                sceneConfigHandler.getScene().setCreateTime(null);
            }
            sceneConfigHandler.save();
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(null, SceneErrorCode.SCENE_NOT_EXIST);
        }
        return new ServiceResult<>(sceneConfigHandler.toData());
    }

    public ServiceResult<Boolean> delete(String id, boolean logic, SceneConfigContextBO context) {
        SceneConfigHandler sceneConfigHandler;
        try {
            sceneConfigHandler = new SceneConfigHandler(id, null, false, context);
            if (logic) {
                sceneConfigHandler.logicDelete();
            } else {
                sceneConfigHandler.delete();
            }
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(false, SceneErrorCode.SCENE_NOT_EXIST);
        }
        return new ServiceResult<>(true);
    }

    public ServiceResult<Integer> bulkDelete(String[] ids, boolean logic, SceneConfigContextBO context) {
        var result = 0;
        for (String id: ids) {
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

}
