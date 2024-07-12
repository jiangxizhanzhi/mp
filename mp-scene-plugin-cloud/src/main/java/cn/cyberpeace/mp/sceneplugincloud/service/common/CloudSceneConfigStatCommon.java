 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudSceneConfigStatCommon.java.
 // File size 5052.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.service.common;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.scene.model.SceneConfigElementModel;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPortModel;
import cn.cyberpeace.mp.scene.model.SceneConfigModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementPortModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementPortModel;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConfigElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConfigElementPortMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName CloudSceneConfigStatCommon
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudSceneStatCommonBase
 */
@Slf4j
@Component
public class CloudSceneConfigStatCommon extends CloudSceneStatCommonBase<CloudPluginSceneConfigElementMapper,
        CloudPluginSceneConfigElementPortMapper> {

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
                .link(SceneConfigModel.class, CloudPluginSceneConfigElementModel.class)
                .filterLink(SceneConfigModel.class, SceneConfigModel::getId,
                        CloudPluginSceneConfigElementModel.class, CloudPluginSceneConfigElementModel::getScene)
                .currentLink(SceneConfigModel.class)
                .filterEqual(SceneConfigModel::getDeleted, false);
        if (!CollectionUtils.isEmpty(sceneIds)) {
            condition.filterIn(SceneConfigModel::getId, sceneIds.toArray(new String[0]));
        }

        condition.currentLink(CloudPluginSceneConfigElementModel.class);
        if (!CollectionUtils.isEmpty(nodeIds)) {
            condition.filterIn(CloudPluginSceneConfigElementModel::getSceneElement, nodeIds.toArray(new String[0]));
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
                .link(SceneConfigModel.class, SceneConfigElementModel.class,
                        SceneConfigElementPortModel.class, CloudPluginSceneConfigElementPortModel.class)
                .filterLink(SceneConfigModel.class, SceneConfigModel::getId,
                        SceneConfigElementModel.class, SceneConfigElementModel::getScene)
                .filterLink(SceneConfigModel.class, SceneConfigModel::getId,
                        SceneConfigElementPortModel.class, SceneConfigElementPortModel::getScene)
                .filterLink(SceneConfigModel.class, SceneConfigModel::getId,
                        CloudPluginSceneElementPortModel.class, CloudPluginSceneElementPortModel::getScene)
                .filterLink(SceneConfigElementModel.class, SceneConfigElementModel::getId,
                        SceneConfigElementPortModel.class, SceneConfigElementPortModel::getSceneElement)
                .filterLink(SceneConfigElementPortModel.class, SceneConfigElementPortModel::getPortId,
                        CloudPluginSceneConfigElementPortModel.class, CloudPluginSceneConfigElementPortModel::getSceneElementPort)
                .currentLink(SceneConfigModel.class)
                .filterEqual(SceneConfigModel::getDeleted, false);
        if (!CollectionUtils.isEmpty(sceneIds)) {
            condition.filterIn(SceneConfigModel::getId, sceneIds.toArray(new String[0]));
        }

        condition.currentLink(SceneConfigElementModel.class);
        if (!CollectionUtils.isEmpty(nodeIds)) {
            condition.filterIn(SceneConfigElementModel::getNodeId, nodeIds.toArray(new String[0]));
        }
        return condition;
    }

    /**
     * CloudSceneConfigStatCommon
     *
     * @param cloudPluginSceneConfigElementMapper
     * @param cloudPluginSceneConfigElementPortMapper
     */
    @Autowired
    public CloudSceneConfigStatCommon(CloudPluginSceneConfigElementMapper cloudPluginSceneConfigElementMapper,
                                      CloudPluginSceneConfigElementPortMapper cloudPluginSceneConfigElementPortMapper) {
        setCloudElementMapper(cloudPluginSceneConfigElementMapper);
        setCloudElementPortMapper(cloudPluginSceneConfigElementPortMapper);
    }

}
