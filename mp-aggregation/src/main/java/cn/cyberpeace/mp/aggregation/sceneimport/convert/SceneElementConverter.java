package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.SceneElementParseResult;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigBO;


class SceneElementConverter implements IConverter {

    private SceneElementParseResult parseResult;

    public SceneElementConverter(SceneElementParseResult parseResult) {
        this.parseResult = parseResult;
    }

    public void convert2BO(ConvertDataContainer container) {
        SceneConfigBO sceneConfigBO = this.createSceneConfigBO(this.parseResult);
        container.setSceneConfigBO(sceneConfigBO);

        CloudPluginSceneConfigBO cloudPlugin = new CloudPluginSceneConfigBO();
        container.setPluginSceneConfigBO(cloudPlugin);
    }

    private SceneConfigBO createSceneConfigBO(SceneElementParseResult scene) {
        if (scene == null) {
            return null;
        }
        SceneConfigBO sceneConfigBO = new SceneConfigBO();
        sceneConfigBO.setName(scene.getName());
        sceneConfigBO.setDescription(scene.getDesc());
        
        sceneConfigBO.setDisplayContext(scene.getDisplayContext());
        return sceneConfigBO;
    }
}
