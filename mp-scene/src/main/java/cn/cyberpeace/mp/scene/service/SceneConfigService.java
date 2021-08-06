package cn.cyberpeace.mp.scene.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scene.constant.SceneErrorCode;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.exception.SceneNotExistException;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import cn.cyberpeace.mp.scene.model.SceneConfigModel;
import cn.cyberpeace.mp.scene.repository.SceneConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class SceneConfigService extends BaseModelServiceImpl<SceneConfigModel, SceneConfigMapper, BaseModelFields,
        QueryParams> {

    public ServiceResult<SceneConfigDataBO> sync(SceneConfigDataBO sceneConfigDataBO, boolean isAreaMode) {
        SceneConfigHandler sceneConfigHandler;
        try {
            sceneConfigHandler = new SceneConfigHandler(sceneConfigDataBO, isAreaMode);
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(null, SceneErrorCode.SCENE_NOT_EXIST);
        }
        sceneConfigHandler.save();
        return new ServiceResult<>(sceneConfigHandler.toData());
    }

    @Override
    public ServiceResult<Boolean> delete(String id) {
        SceneConfigHandler sceneConfigHandler;
        try {
            sceneConfigHandler = new SceneConfigHandler(id, null, false);
        } catch (SceneNotExistException e) {
            return new ServiceResult<>(false, SceneErrorCode.SCENE_NOT_EXIST);
        }
        sceneConfigHandler.delete();
        return new ServiceResult<>(true);
    }

    @Override
    public ServiceResult<Integer> bulkDelete(String[] ids) {
        var result = 0;
        for (String id: ids) {
            if (Boolean.TRUE.equals(delete(id).getSource())) {
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
