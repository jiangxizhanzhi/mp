package cn.cyberpeace.mp.sceneplugincloud.service.common;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CloudSceneStatCommon extends CloudSceneStatCommonBase<CloudPluginSceneElementMapper,
        CloudPluginSceneElementPortMapper> {

    @Override
    protected HumanCondition genNodeCondition(List<String> sceneIds, List<String> nodeIds) {
        var condition = new HumanCondition()
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

    @Override
    protected HumanCondition genNodePortCondition(List<String> sceneIds, List<String> nodeIds) {
        var condition = new HumanCondition()
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

    @Autowired
    public CloudSceneStatCommon(CloudPluginSceneElementMapper cloudPluginSceneElementMapper,
                                CloudPluginSceneElementPortMapper cloudPluginSceneElementPortMapper) {
        setCloudElementMapper(cloudPluginSceneElementMapper);
        setCloudElementPortMapper(cloudPluginSceneElementPortMapper);
    }

    /**
     * TODO 分组查询host的聚合统计数量
     * @param sceneDataBOs 场景列表
     * @param nodeIds 节点id列表，不传默认为全部
     * @param groupFieldName 分组聚合字段
     * @return 字段的聚合统计数量 Map<字段值, 该字段值统计数量>
     */
    public Map<String, Long> groupCountElementFromData(List<SceneDataBO> sceneDataBOs, List<String> nodeIds,
                                                       String groupFieldName) {
        for (var sceneDataBO : sceneDataBOs) {
            var sceneHandler = new SceneHandler(sceneDataBO, null);
            var scene = sceneHandler.getScene();
            CloudScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
            pluginHandler.getElementMap().forEach((nodeId, cloudElement) -> {

            });
        }
        return new HashMap<>();
    }

}
