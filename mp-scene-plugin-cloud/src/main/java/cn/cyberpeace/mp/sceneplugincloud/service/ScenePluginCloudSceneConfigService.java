package cn.cyberpeace.mp.sceneplugincloud.service;

import cn.cyberpeace.mp.base.common.StatCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.scene.model.SceneConfigModel;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudErrorCode;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConfigElementMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ScenePluginCloudSceneConfigService {

    public static final List<String> AVAILABLE_CLOUD_ELEMENT_FILTER_IN_FIELDS = Arrays.asList(
            BeanUtils.getBeanGetterFieldNames(
                    BaseCloudPluginSceneElementModel::getHost,
                    BaseCloudPluginSceneElementModel::getVirtualType
            )
    );

    private CloudPluginSceneConfigElementMapper cloudPluginSceneConfigElementMapper;

    /**
     * 分组查询指定字段的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param filterIn in过滤条件 Map<字段, 字段值范围>
     * @param groupFieldName 分组聚合字段
     * @param <T> 分组聚合字段的值类型
     * @return 字段的聚合统计数量 Map<字段值, 该字段值统计数量>
     */
    private <T> ServiceResult<Map<T, Integer>> groupCountElement(List<String> sceneIds, List<String> nodeIds,
                                                                 Map<String, List<?>> filterIn, String groupFieldName) {
        var condition = new HumanCondition()
                .link(SceneConfigModel.class, CloudPluginSceneConfigElementModel.class)
                .filterLink(SceneConfigModel.class, SceneConfigModel::getId,
                        CloudPluginSceneConfigElementModel.class, CloudPluginSceneConfigElementModel::getScene)
                .currentLink(SceneConfigModel.class)
                .filterEqual(SceneConfigModel::getDeleted, false)
                .currentLink(CloudPluginSceneConfigElementModel.class);
        if (!CollectionUtils.isEmpty(sceneIds)) {
            condition.filterIn(BaseCloudPluginSceneElementModel::getScene, sceneIds.toArray(new String[0]));
        }
        if (!CollectionUtils.isEmpty(nodeIds)) {
            condition.filterIn(BaseCloudPluginSceneElementModel::getSceneElement, nodeIds.toArray(new String[0]));
        }
        if (filterIn != null) {
            filterIn.forEach((fieldName, value) -> {
                if (AVAILABLE_CLOUD_ELEMENT_FILTER_IN_FIELDS.contains(fieldName)) {
                    condition.filterIn(fieldName, value.toArray());
                }
            });
        }

        Map<T, Integer> result;
        try {
            result = StatCommon.groupCount(cloudPluginSceneConfigElementMapper, condition, groupFieldName);
        } catch (Exception e) {
            log.error("Group count cloud element on field {} error: {}", groupFieldName, e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, e);
        }

        return new ServiceResult<>(result);
    }

    @Autowired
    public ScenePluginCloudSceneConfigService(CloudPluginSceneConfigElementMapper cloudPluginSceneConfigElementMapper) {
        this.cloudPluginSceneConfigElementMapper = cloudPluginSceneConfigElementMapper;
    }

    /**
     * 分组查询host的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param filterIn in过滤条件 Map<字段, 字段值范围>
     * @return 字段的聚合统计数量 Map<host, 该字段值统计数量>
     */
    public ServiceResult<Map<String, Integer>> groupCountElementByHost(
            List<String> sceneIds, List<String> nodeIds, Map<String, List<?>> filterIn) {
        return groupCountElement(sceneIds, nodeIds, filterIn,
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getHost));
    }

    /**
     * 分组查询虚拟类型的聚合统计数量
     * @param sceneIds 场景id列表，不传默认为全部
     * @param nodeIds 节点id列表，不传默认为全部
     * @param filterIn in过滤条件 Map<字段, 字段值范围>
     * @return 字段的聚合统计数量 Map<virtualType, 该字段值统计数量>
     */
    public ServiceResult<Map<Integer, Integer>> groupCountElementByVirtualType(
            List<String> sceneIds, List<String> nodeIds, Map<String, List<?>> filterIn) {
        return groupCountElement(sceneIds, nodeIds, filterIn,
                BeanUtils.getBeanGetterFieldName(BaseCloudPluginSceneElementModel::getVirtualType));
    }

}
