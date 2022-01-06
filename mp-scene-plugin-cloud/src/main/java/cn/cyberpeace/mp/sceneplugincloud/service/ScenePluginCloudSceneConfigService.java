package cn.cyberpeace.mp.sceneplugincloud.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConfigMapper;
import cn.cyberpeace.mp.sceneplugincloud.service.common.CloudSceneConfigStatCommon;
import lombok.extern.slf4j.Slf4j;
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

    @Autowired
    public ScenePluginCloudSceneConfigService(CloudSceneConfigStatCommon cloudSceneConfigStatCommon, CloudPluginSceneConfigMapper cloudPluginSceneConfigMapper) {
        this.cloudSceneConfigStatCommon = cloudSceneConfigStatCommon;
        this.cloudPluginSceneConfigMapper = cloudPluginSceneConfigMapper;
    }

    /**
     * 查询网元指定字段的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param fieldNames 聚合统计字段  有效字段 COUNT_ELEMENT_FIELDS
     * @return 字段的聚合统计数量 Map<字段名称, Map<字段值, 该字段统计数量>>
     */
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
                result.put(fieldName, cloudSceneConfigStatCommon.groupCountElement(sceneIds, nodeIds, fieldName, fieldName, false));
            });
        }

        return new ServiceResult<>(result);
    }

    public ServiceResult<Boolean> updateEnableRecording(String sceneId, boolean newValue) {
        CloudPluginSceneConfigModel sceneModel = new CloudPluginSceneConfigModel();
        sceneModel.setEnableRecording(newValue);
        int result = cloudPluginSceneConfigMapper.filterUpdate(sceneModel, new HumanCondition().filterEqual(CloudPluginSceneModel::getScene, sceneId));
        if (result == 0) {
            sceneModel.setScene(sceneId);
            cloudPluginSceneConfigMapper.save(sceneModel);
        }
        return new ServiceResult<>(result > 0);
    }
}
