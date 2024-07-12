 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneElementConverter.java.
 // File size 1724.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.SceneElementParseResult;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigBO;

/**
 * @ClassName SceneElementConverter
 * @Description 将解析出的scene节点转成BO
 * @Author maojun
 * @Date 2021/10/12
 * @Version 1.0.0
 * @see IConverter
 */
class SceneElementConverter implements IConverter {

    private SceneElementParseResult parseResult;

    /**
     * SceneElementConverter
     *
     * @param parseResult
     */
    public SceneElementConverter(SceneElementParseResult parseResult) {
        this.parseResult = parseResult;
    }

    /**
     * convert2BO
     *
     * @param container
     */
    public void convert2BO(ConvertDataContainer container) {
        SceneConfigBO sceneConfigBO = this.createSceneConfigBO(this.parseResult);
        container.setSceneConfigBO(sceneConfigBO);

        CloudPluginSceneConfigBO cloudPlugin = new CloudPluginSceneConfigBO();
        container.setPluginSceneConfigBO(cloudPlugin);
    }

    /**
     * createSceneConfigBO
     *
     * @param scene
     * @return
     */
    private SceneConfigBO createSceneConfigBO(SceneElementParseResult scene) {
        if (scene == null) {
            return null;
        }
        SceneConfigBO sceneConfigBO = new SceneConfigBO();
        sceneConfigBO.setName(scene.getName());
        sceneConfigBO.setDescription(scene.getDesc());
        // 传递场景信息
        sceneConfigBO.setDisplayContext(scene.getDisplayContext());
        return sceneConfigBO;
    }
}
