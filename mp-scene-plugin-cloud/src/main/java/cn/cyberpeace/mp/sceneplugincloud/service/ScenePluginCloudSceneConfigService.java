package cn.cyberpeace.mp.sceneplugincloud.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudSceneConfigPluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConfigElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConfigMapper;
import cn.cyberpeace.mp.sceneplugincloud.service.common.CloudSceneConfigStatCommon;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ScenePluginCloudSceneConfigService {

    public static final List<String> COUNT_ELEMENT_FIELDS = Arrays.asList(
            BeanUtils.getBeanGetterFieldNames(
                    BaseCloudPluginSceneElementModel::getHost,
                    BaseCloudPluginSceneElementModel::getVirtualType
            )
    );

    private CloudSceneConfigStatCommon cloudSceneConfigStatCommon;

    private CloudPluginSceneConfigMapper cloudPluginSceneConfigMapper;

    private CloudPluginSceneConfigElementMapper cloudPluginSceneConfigElementMapper;

    @Autowired
    public ScenePluginCloudSceneConfigService(CloudSceneConfigStatCommon cloudSceneConfigStatCommon, CloudPluginSceneConfigMapper cloudPluginSceneConfigMapper,
                                              CloudPluginSceneConfigElementMapper cloudPluginSceneConfigElementMapper) {
        this.cloudSceneConfigStatCommon = cloudSceneConfigStatCommon;
        this.cloudPluginSceneConfigMapper = cloudPluginSceneConfigMapper;
        this.cloudPluginSceneConfigElementMapper = cloudPluginSceneConfigElementMapper;
    }

    
    public ServiceResult<Map<String, Map<Object, Long>>> groupCountElement(
            List<String> sceneIds, List<String> nodeIds, List<String> fieldNames) {
        List<String> countFieldNames = new ArrayList<>();
        fieldNames.forEach(fieldName -> {
            if (COUNT_ELEMENT_FIELDS.contains(fieldName)) {
                countFieldNames.add(fieldName);
            }
        });

        Map<String, Map<Object, Long>> result = new HashMap<>();
        if (!countFieldNames.isEmpty()) {
            countFieldNames.forEach(fieldName -> {
                var countResult = cloudSceneConfigStatCommon.groupCountElement(sceneIds, nodeIds, fieldName, List.of(fieldName), false);
                result.put(fieldName, countResult.get(fieldName));
            });
        }

        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> updateEnableRecording(String sceneId, boolean newValue) {
        SceneConfigHandler sceneConfigHandler = new SceneConfigHandler(sceneId, null, false, null);
        CloudSceneConfigPluginHandler cloudSceneConfigPluginHandler = sceneConfigHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        CloudPluginSceneConfigBO sceneBO = cloudSceneConfigPluginHandler.getScene();
        CloudPluginSceneConfigModel sceneModel = new CloudPluginSceneConfigModel();
        sceneModel.setEnableRecording(newValue);
        sceneModel.setEnableRate(sceneBO != null ? sceneBO.getEnableRate() : false);
        int result = cloudPluginSceneConfigMapper.filterUpdate(sceneModel, new HumanCondition().filterEqual(CloudPluginSceneModel::getScene, sceneId));
        if (result == 0) {
            sceneModel.setScene(sceneId);
            cloudPluginSceneConfigMapper.save(sceneModel);
        }
        return new ServiceResult<>(result > 0);
    }

    public ServiceResult<Boolean> updateElementImage(String sceneId, String nodeId, String image) {
        if (StringUtils.isEmpty(sceneId) || StringUtils.isEmpty(nodeId) || StringUtils.isEmpty(image)) {
            return new ServiceResult<>(false);
        }
        CloudPluginSceneConfigElementModel elementModel = new CloudPluginSceneConfigElementModel();
        elementModel.setImage(image);
        int result = cloudPluginSceneConfigElementMapper.filterUpdate(elementModel, new HumanCondition()
                .filterEqual(CloudPluginSceneConfigElementModel::getScene, sceneId)
                .filterEqual(CloudPluginSceneConfigElementModel::getSceneElement, nodeId)
        );
        return new ServiceResult<>(result > 0);
    }
}
