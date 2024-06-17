package cn.cyberpeace.mp.scenepluginconfig.plugin;

import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.scenepluginconfig.bo.ConfigPluginSceneBO;
import cn.cyberpeace.mp.scenepluginconfig.bo.ConfigPluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginconfig.bo.ConfigPluginSceneElementPortBO;
import cn.cyberpeace.mp.scenepluginconfig.constant.PluginConstant;
import cn.cyberpeace.mp.scenepluginconfig.plugin.common.SceneHandlerUtils;
import cn.cyberpeace.mp.scenepluginconfig.plugin.lifecyclehandler.CreateHandler;
import cn.cyberpeace.mp.scenepluginconfig.plugin.lifecyclehandler.DeleteHandler;
import cn.cyberpeace.mp.scenepluginconfig.plugin.lifecyclehandler.ModifyHandler;
import cn.cyberpeace.mp.scenepluginconfig.plugin.modelhandler.ConfigScenePluginModelHandler;
import lombok.Data;
import org.springframework.stereotype.Component;


@ClassFieldClasses({
        ConfigPluginSceneBO.class,
        ConfigPluginSceneElementBO.class,
        ConfigPluginSceneElementPortBO.class,
        Object.class,
})
@Component
@PluginKey(value = PluginConstant.PLUGIN_KEY, priority = PluginConstant.PLUGIN_PRIORITY)
@Data
public class ConfigScenePluginHandler extends ScenePluginHandler<ConfigPluginSceneBO,
        ConfigPluginSceneElementBO, ConfigPluginSceneElementPortBO, Object> {

    private ConfigScenePluginModelHandler pluginModelHandler;

    private SceneHandlerUtils handlerUtils;
    private CreateHandler createHandler;
    private DeleteHandler deleteHandler;

    @Override
    public void correctData() {
        if (getScene() == null) {
            setScene(ClassUtils.createInstance(getSceneBOClass()));
        }
    }

    @Override
    public void fromData(ScenePluginDataBO pluginData) {
        super.fromData(pluginData);
        var scene = getScene();
        if (scene != null) {
            scene.setId(null);
        }
    }

    @Override
    public void initBase() {
        super.initBase();
        pluginModelHandler = new ConfigScenePluginModelHandler(this);
        handlerUtils = new SceneHandlerUtils(this);
        createHandler = new CreateHandler(this);
        deleteHandler = new DeleteHandler(this);
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
    public long getTaskCount() {
        CloudScenePluginHandler cloudPluginHandler = getSceneHandler().getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        return cloudPluginHandler.getElementMap().values().stream().filter(cloudElement -> !cloudElement.getDeriveNetworks().isEmpty()).count();
    }

    @Override
    public void create() {
        boolean flag = this.isFullEntityScene();
        if (flag) {
            return;
        }
        createHandler.create();
    }

    @Override
    public void modify(SceneHandler originSceneHandler) {
        new ModifyHandler(originSceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY), this).modify();
    }

    @Override
    public void delete() {
        deleteHandler.delete();
    }

    private boolean isFullEntityScene() {
        CloudScenePluginHandler cloudPlugin = getSceneHandler().getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        boolean flag = true;
        for (var cloudElement : cloudPlugin.getElementMap().values()) {
            if (!BaseCloudPluginSceneElementModel.ImageType.ENTITY.equals(cloudElement.getImageType())
                    && !BaseCloudPluginSceneElementModel.ImageType.THIRD_TARGET.equals(cloudElement.getImageType())) {
                flag = false;
                break;
            }
        }
        return flag;
    }
}
