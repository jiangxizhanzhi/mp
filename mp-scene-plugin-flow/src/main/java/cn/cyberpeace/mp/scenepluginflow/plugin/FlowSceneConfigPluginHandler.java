 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowSceneConfigPluginHandler.java.
 // File size 3658.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.plugin;

import cn.cyberpeace.mp.scene.core.plugin.SceneConfigPluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneConfigBO;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneConfigElementBO;
import cn.cyberpeace.mp.scenepluginflow.bo.FlowPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.scenepluginflow.constant.PluginConstant;
import cn.cyberpeace.mp.scenepluginflow.plugin.modelhandler.FlowSceneConfigPluginModelHandler;
import org.springframework.stereotype.Component;


@Component
@PluginKey(value = PluginConstant.PLUGIN_KEY, priority = PluginConstant.PLUGIN_PRIORITY)
public class FlowSceneConfigPluginHandler extends SceneConfigPluginHandler<FlowPluginSceneConfigBO,
        FlowPluginSceneConfigElementBO, FlowPluginSceneConfigElementPortBO, Object> {

    private FlowSceneConfigPluginModelHandler pluginModelHandler;

    /**
     * initBase
     */
    @Override
    public void initBase() {
        super.initBase();
        pluginModelHandler = new FlowSceneConfigPluginModelHandler(this);
    }

    /**
     * reloadData
     */
    @Override
    public void reloadData() {
        setScene(null);
        getElementMap().clear();
        getElementPortMap().clear();

        pluginModelHandler.loadScene(pluginModelHandler.readScene());
        pluginModelHandler.loadElements(pluginModelHandler.readElements(getElementIds().toArray(new String[0])));
        pluginModelHandler.loadElementPorts(pluginModelHandler.readElementPorts(getElementPortIds()
                .toArray(new String[0])));
    }

    /**
     * saveScene
     */
    @Override
    public void saveScene() {
        pluginModelHandler.saveScene();
    }

    /**
     * deleteScene
     */
    @Override
    public void deleteScene() {
        pluginModelHandler.deleteScene();
    }

    /**
     * createElements
     *
     * @param elementIds
     */
    @Override
    public void createElements(String... elementIds) {
        pluginModelHandler.createElements(elementIds);
    }

    /**
     * updateElements
     *
     * @param elementIds
     */
    @Override
    public void updateElements(String... elementIds) {
        pluginModelHandler.updateElements(elementIds);
    }

    /**
     * deleteElements
     *
     * @param elementIds
     */
    @Override
    public void deleteElements(String... elementIds) {
        pluginModelHandler.deleteElements(elementIds);
    }

    /**
     * createElementPorts
     *
     * @param elementPortIds
     */
    @Override
    public void createElementPorts(String... elementPortIds) {
        pluginModelHandler.createElementPorts(elementPortIds);
    }

    /**
     * updateElementPorts
     *
     * @param elementPortIds
     */
    @Override
    public void updateElementPorts(String... elementPortIds) {
        pluginModelHandler.updateElementPorts(elementPortIds);
    }

    /**
     * deleteElementPorts
     *
     * @param elementPortIds
     */
    @Override
    public void deleteElementPorts(String... elementPortIds) {
        pluginModelHandler.deleteElementPorts(elementPortIds);
    }

    /**
     * initClass
     */
    @Override
    protected void initClass() {
        super.sceneBOClass = FlowPluginSceneConfigBO.class;
        super.elementBOClass = FlowPluginSceneConfigElementBO.class;
        super.elementPortBOClass = FlowPluginSceneConfigElementPortBO.class;
        super.contextClass = Object.class;
    }
}
