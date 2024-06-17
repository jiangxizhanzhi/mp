package cn.cyberpeace.mp.scenepluginconfig.service;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scenepluginconfig.bo.ConfigPluginSceneConfigElementBO;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigPluginSceneConfigElementModel;
import cn.cyberpeace.mp.scenepluginconfig.repository.ConfigPluginSceneConfigElementMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ScenePluginConfigSceneConfigService {
    @Resource
    private ConfigPluginSceneConfigElementMapper configPluginSceneConfigElementMapper;

    public ServiceResult<Boolean> create(ConfigPluginSceneConfigElementBO configPluginSceneConfigElementBO) {
        var model = configPluginSceneConfigElementBO.toModel();
        configPluginSceneConfigElementMapper.create(model);
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> update(ConfigPluginSceneConfigElementBO configPluginSceneConfigElementBO) {
        if (StringUtils.isEmpty(configPluginSceneConfigElementBO.getId())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        var model = configPluginSceneConfigElementBO.toModel();
        configPluginSceneConfigElementMapper.update(model);
        return new ServiceResult<>(true);
    }

    public ServiceResult<ConfigPluginSceneConfigElementBO> getBySceneId(String sceneId) {
        HumanCondition humanCondition = new HumanCondition().filterEqual(ConfigPluginSceneConfigElementModel::getScene, sceneId);
        var models = configPluginSceneConfigElementMapper.list(humanCondition);
        if (CollectionUtils.isEmpty(models)) {
            return new ServiceResult<>();
        }
        ConfigPluginSceneConfigElementBO configElementBO = new ConfigPluginSceneConfigElementBO();
        configElementBO.fromModel(models.get(0));
        return new ServiceResult<>(configElementBO);
    }
}
