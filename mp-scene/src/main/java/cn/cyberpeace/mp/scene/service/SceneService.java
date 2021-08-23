package cn.cyberpeace.mp.scene.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scene.constant.SceneErrorCode;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import cn.cyberpeace.mp.scene.core.exception.SceneNotExistException;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class SceneService {

    public ServiceResult<SceneDataBO> get(String id, boolean isAreaMode, String areaId) {
        SceneHandler sceneHandler;
        try {
            sceneHandler = new SceneHandler(id, areaId, isAreaMode);
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(null, SceneErrorCode.SCENE_NOT_EXIST);
        }
        return new ServiceResult<>(sceneHandler.toData());
    }

    public ServiceResult<Boolean> delete(String id, boolean logic) {
        SceneHandler sceneHandler;
        try {
            sceneHandler = new SceneHandler(id, null, false);
            if (logic) {
                sceneHandler.logicDelete();
            } else {
                sceneHandler.delete();
            }
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(false, SceneErrorCode.SCENE_NOT_EXIST);
        }
        return new ServiceResult<>(true);
    }

    public ServiceResult<Integer> bulkDelete(String[] ids, boolean logic) {
        var result = 0;
        for (String id: ids) {
            if (Boolean.TRUE.equals(delete(id, logic).getSource())) {
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
