package cn.cyberpeace.mp.sceneplugingns3.plugin;

import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.sceneplugingns3.bo.Gns3PluginSceneBO;
import cn.cyberpeace.mp.sceneplugingns3.bo.Gns3PluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugingns3.bo.Gns3PluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugingns3.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugingns3.plugin.common.SceneHandlerUtils;
import cn.cyberpeace.mp.sceneplugingns3.plugin.lifecyclehandler.CreateHandler;
import cn.cyberpeace.mp.sceneplugingns3.plugin.lifecyclehandler.DeleteHandler;
import cn.cyberpeace.mp.sceneplugingns3.plugin.lifecyclehandler.InitiateHandler;
import cn.cyberpeace.mp.sceneplugingns3.plugin.modelhandler.Gns3ScenePluginModelHandler;
import lombok.Data;
import org.springframework.stereotype.Component;


@ClassFieldClasses({
        Gns3PluginSceneBO.class,
        Gns3PluginSceneElementBO.class,
        Gns3PluginSceneElementPortBO.class,
        Object.class,
})
@Component
@PluginKey(value = PluginConstant.PLUGIN_KEY, priority = PluginConstant.PLUGIN_PRIORITY)
@Data
public class Gns3ScenePluginHandler extends ScenePluginHandler<Gns3PluginSceneBO,
        Gns3PluginSceneElementBO, Gns3PluginSceneElementPortBO, Object> {

    private Gns3ScenePluginModelHandler pluginModelHandler;

    private SceneHandlerUtils handlerUtils;

    private CreateHandler createHandler;
    private InitiateHandler initiateHandler;
    private DeleteHandler deleteHandler;

    @Override
    public void initBase() {
        super.initBase();
        this.pluginModelHandler = new Gns3ScenePluginModelHandler(this);
        this.handlerUtils = new SceneHandlerUtils(this);
        this.initiateHandler = new InitiateHandler(this);
        this.createHandler = new CreateHandler(this);
        this.deleteHandler = new DeleteHandler(this);
    }

    @Override
    public void saveScene() {
        pluginModelHandler.saveScene();
    }

    @Override
    public void deleteScene() {
        pluginModelHandler.deleteScene();
    }

    @Override
    public void createElements(String... elementIds) {
        pluginModelHandler.createElements(elementIds);
    }

    @Override
    public void updateElements(String... elementIds) {
        pluginModelHandler.updateElements(elementIds);
    }

    @Override
    public void deleteElements(String... elementIds) {
        pluginModelHandler.deleteElements(elementIds);
    }

    @Override
    public void createElementPorts(String... elementPortIds) {
        pluginModelHandler.createElementPorts(elementPortIds);
    }

    @Override
    public void updateElementPorts(String... elementPortIds) {
        pluginModelHandler.updateElementPorts(elementPortIds);
    }

    @Override
    public void deleteElementPorts(String... elementPortIds) {
        pluginModelHandler.deleteElementPorts(elementPortIds);
    }

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

    @Override
    public void initiate() throws Exception {
        initiateHandler.initiate();
    }

    @Override
    public void create() throws Exception {
        createHandler.create();
    }

    @Override
    public void delete() throws Exception {
        deleteHandler.delete();
    }
}
