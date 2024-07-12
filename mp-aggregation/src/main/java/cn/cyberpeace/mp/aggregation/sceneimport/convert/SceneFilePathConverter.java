 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneFilePathConverter.java.
 // File size 1549.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @ClassName SceneFilePathConverter
 * @Description 写入images目录中解析出来的镜像信息
 * @Author maojun
 * @Date 2021/10/12
 * @Version 1.0.0
 * @see IConverter
 */
class SceneFilePathConverter implements IConverter {

    /**
     * convert2BO
     *
     * @param container
     */
    @Override
    public void convert2BO(ConvertDataContainer container) {
        String sceneFilePath = container.getSceneFilePath();
        if (StringUtils.isEmpty(sceneFilePath)) {
            return;
        }
        Map<String, CloudPluginSceneConfigElementBO> cloudElementMap = container.getCloudPluginElements();
        if (CollectionUtils.isNotEmpty(container.getNodeElements())) {
            for (SceneConfigElementBO e : container.getNodeElements()) {
                CloudPluginSceneConfigElementBO cloudElement = cloudElementMap.get(e.getId());
                if (cloudElement.getRole() != null && cloudElement.getRole() == BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL) {
                    e.setFile(sceneFilePath);
                }
            }
        }
    }
}
