 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudSceneStatCommon.java.
 // File size 11016.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.service.common;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.basic.MapUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneElementPortModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementPortModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementPortMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @ClassName CloudSceneStatCommon
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudSceneStatCommonBase
 */
@Slf4j
@Component
public class CloudSceneStatCommon extends CloudSceneStatCommonBase<CloudPluginSceneElementMapper,
        CloudPluginSceneElementPortMapper> {

    /**
     * genNodeCondition
     *
     * @param sceneIds
     * @param nodeIds
     * @return 查询条件构造器
     */
    @Override
    protected HumanCondition genNodeCondition(List<String> sceneIds, List<String> nodeIds) {
        HumanCondition condition = new HumanCondition()
                .link(SceneModel.class, CloudPluginSceneElementModel.class)
                .filterLink(SceneModel.class, SceneModel::getId,
                        CloudPluginSceneElementModel.class, CloudPluginSceneElementModel::getScene)
                .currentLink(SceneModel.class)
                .filterEqual(SceneModel::getDeleted, false);
        if (!CollectionUtils.isEmpty(sceneIds)) {
            condition.filterIn(SceneModel::getId, sceneIds.toArray(new String[0]));
        }

        condition.currentLink(CloudPluginSceneElementModel.class);
        if (!CollectionUtils.isEmpty(nodeIds)) {
            condition.filterIn(CloudPluginSceneElementModel::getSceneElement, nodeIds.toArray(new String[0]));
        }
        return condition;
    }

    /**
     * genNodePortCondition
     *
     * @param sceneIds
     * @param nodeIds
     * @return 查询条件构造器
     */
    @Override
    protected HumanCondition genNodePortCondition(List<String> sceneIds, List<String> nodeIds) {
        HumanCondition condition = new HumanCondition()
                .link(SceneModel.class, SceneElementModel.class, SceneElementPortModel.class, CloudPluginSceneElementPortModel.class)
                .filterLink(SceneModel.class, SceneModel::getId,
                        SceneElementModel.class, SceneElementModel::getScene)
                .filterLink(SceneModel.class, SceneModel::getId,
                        SceneElementPortModel.class, SceneElementPortModel::getScene)
                .filterLink(SceneModel.class, SceneModel::getId,
                        CloudPluginSceneElementPortModel.class, CloudPluginSceneElementPortModel::getScene)
                .filterLink(SceneElementModel.class, SceneElementModel::getId,
                        SceneElementPortModel.class, SceneElementPortModel::getSceneElement)
                .filterLink(SceneElementPortModel.class, SceneElementPortModel::getPortId,
                        CloudPluginSceneElementPortModel.class, CloudPluginSceneElementPortModel::getSceneElementPort)
                .currentLink(SceneModel.class)
                .filterEqual(SceneModel::getDeleted, false);
        if (!CollectionUtils.isEmpty(sceneIds)) {
            condition.filterIn(SceneModel::getId, sceneIds.toArray(new String[0]));
        }

        condition.currentLink(SceneElementModel.class);
        if (!CollectionUtils.isEmpty(nodeIds)) {
            condition.filterIn(SceneElementModel::getNodeId, nodeIds.toArray(new String[0]));
        }
        return condition;
    }

    /**
     * CloudSceneStatCommon
     *
     * @param cloudPluginSceneElementMapper
     * @param cloudPluginSceneElementPortMapper
     */
    @Autowired
    public CloudSceneStatCommon(CloudPluginSceneElementMapper cloudPluginSceneElementMapper,
                                CloudPluginSceneElementPortMapper cloudPluginSceneElementPortMapper) {
        setCloudElementMapper(cloudPluginSceneElementMapper);
        setCloudElementPortMapper(cloudPluginSceneElementPortMapper);
    }

    /**
     * 简单从聚合统计数据源计算计数统计结果
     * @param collectedStatData 聚合统计数据源
     * @param distinct 是否去重
     * @return 字段的聚合统计数量 Map<key, 计数统计结果>
     */
    public Map<String, Long> countCollectedStatData(Map<String, List<Object>> collectedStatData, boolean distinct) {
        Map<String, Long> result = new HashMap<>();
        collectedStatData.forEach((key, values) -> {
            long count;
            if (distinct) {
                count = new HashSet<>(values).size();
            } else {
                count = values.size();
            }
            result.put(key, count);
        });
        return result;
    }

    /**
     * 分组搜集网元指定字段的聚合统计数据源
     * @param sceneDataBOs 场景列表
     * @param nodeIds 节点id列表，不传默认为全部
     * @param groupFieldName 分组聚合字段
     * @return 字段的聚合统计数据源 Map<统计字段名称, Map<分组字段值, 该统计字段值列表>>
     */
    public Map<String, Map<String, List<Object>>> collectGroupStatElementFromData(
            List<SceneDataBO> sceneDataBOs, List<String> nodeIds, String groupFieldName, List<String> statFieldNames) {
        if (nodeIds != null && nodeIds.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Map<String, List<Object>>> fieldGroupValuesMap = new HashMap<>();
        for (SceneDataBO sceneDataBO : sceneDataBOs) {
            SceneHandler sceneHandler = new SceneHandler(sceneDataBO, null);
            CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
            pluginHandler.getElementMap().forEach((nodeId, cloudElement) -> {
                if (nodeIds != null && !nodeIds.contains(nodeId)) {
                    return;
                }

                Object groupFieldValue = ClassUtils.getObjFieldValue(cloudElement, groupFieldName);
                if (groupFieldValue == null) {
                    return;
                }
                String groupFieldValueStr = groupFieldValue.toString();

                statFieldNames.forEach(statFieldName -> {
                    Object statFieldValue = ClassUtils.getObjFieldValue(cloudElement, statFieldName);
                    if (statFieldValue == null) {
                        return;
                    }
                    Map<String, List<Object>> groupValuesMap = MapUtils.setDefault(fieldGroupValuesMap, statFieldName, new HashMap<>());
                    MapUtils.setDefault(groupValuesMap, groupFieldValueStr, new ArrayList<>()).add(statFieldValue);
                });
            });
        }
        return fieldGroupValuesMap;
    }

    /**
     * 搜集网元指定字段的聚合统计数据源
     * @param sceneDataBOs 场景列表
     * @param nodeIds 节点id列表，不传默认为全部
     * @param fieldNames 聚合统计字段
     * @return 字段的聚合统计数据源 Map<统计字段名称, 该字段值列表>
     */
    public Map<String, List<Object>> collectStatElementFromData(List<SceneDataBO> sceneDataBOs, List<String> nodeIds,
                                                                List<String> fieldNames) {
        if (nodeIds != null && nodeIds.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, List<Object>> fieldNameValuesMap = new HashMap<>();
        for (SceneDataBO sceneDataBO : sceneDataBOs) {
            SceneHandler sceneHandler = new SceneHandler(sceneDataBO, null);
            CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
            pluginHandler.getElementMap().forEach((nodeId, cloudElement) -> {
                if (nodeIds != null && !nodeIds.contains(nodeId)) {
                    return;
                }

                fieldNames.forEach(fieldName -> {
                    Object fieldValue = ClassUtils.getObjFieldValue(cloudElement, fieldName);
                    if (fieldValue == null) {
                        return;
                    }
                    MapUtils.setDefault(fieldNameValuesMap, fieldName, new ArrayList<>()).add(fieldValue);
                });
            });
        }
        return fieldNameValuesMap;
    }

    /**
     * 搜集端口指定字段的聚合统计数据源
     * @param sceneDataBOs 场景列表
     * @param nodeIds 节点id列表，不传默认为全部
     * @param fieldNames 聚合统计字段
     * @return 字段的聚合统计数据源 Map<统计字段名称, 该字段值列表>
     */
    public Map<String, List<Object>> collectStatPortFromData(List<SceneDataBO> sceneDataBOs, List<String> nodeIds,
                                                             List<String> fieldNames) {
        if (nodeIds != null && nodeIds.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, List<Object>> fieldNameValuesMap = new HashMap<>();
        for (SceneDataBO sceneDataBO : sceneDataBOs) {
            SceneHandler sceneHandler = new SceneHandler(sceneDataBO, null);
            CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
            pluginHandler.getElementPortMap().forEach((portId, cloudPort) -> {
                String nodeId = cloudPort.getSrcPort().getElement().getId();
                if (nodeIds != null && !nodeIds.contains(nodeId)) {
                    return;
                }

                fieldNames.forEach(fieldName -> {
                    Object fieldValue = ClassUtils.getObjFieldValue(cloudPort, fieldName);
                    if (fieldValue == null) {
                        return;
                    }
                    MapUtils.setDefault(fieldNameValuesMap, fieldName, new ArrayList<>()).add(fieldValue);
                });
            });
        }
        return fieldNameValuesMap;
    }

}
