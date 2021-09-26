package cn.cyberpeace.mp.sceneplugincloud.plugin;

import cn.cyberpeace.mp.base.annotation.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.DeleteHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.InitiateHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.modelhandler.CloudScenePluginModelHandler;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@ClassFieldClasses({
        CloudPluginSceneBO.class,
        CloudPluginSceneElementBO.class,
        CloudPluginSceneElementPortBO.class,
        CloudScenePluginContext.class,
})
@Component
@PluginKey(value = PluginConstant.PLUGIN_KEY, priority = PluginConstant.PLUGIN_PRIORITY)
@Data
public class CloudScenePluginHandler extends ScenePluginHandler<CloudPluginSceneBO,
        CloudPluginSceneElementBO, CloudPluginSceneElementPortBO, CloudScenePluginContext> {

    private CloudScenePluginModelHandler pluginModelHandler;

    private final Map<String, CloudPluginSceneElementBO> gatewayElementMap = new HashMap<>();
    private final Map<String, CloudPluginSceneElementBO> firewallElementMap = new HashMap<>();
    private final Map<String, CloudPluginSceneElementBO> terminalElementMap = new HashMap<>();

    private InitiateHandler initiateHandler;
    private CreateHandler createHandler;
    private DeleteHandler deleteHandler;

    private void loadRelated() {
        gatewayElementMap.clear();
        firewallElementMap.clear();
        terminalElementMap.clear();
        getElementMap().forEach((nodeId, element) -> {
            var srcElement = getSrcElement(nodeId);
            element.setSrcElement(srcElement);

            Integer elementType = srcElement.getType();
            if (elementType == null) {
                terminalElementMap.put(nodeId, element);
            } else if (elementType.equals(BaseSceneElementModel.Type.SWITCH)
                    || elementType.equals(BaseSceneElementModel.Type.ROUTER)) {
                gatewayElementMap.put(nodeId, element);
            } else if (elementType.equals(BaseSceneElementModel.Type.FIREWALL)) {
                firewallElementMap.put(nodeId, element);
            } else {
                terminalElementMap.put(nodeId, element);
            }

            Integer virtualType = element.getVirtualType();
            if (virtualType == null) {
                return;
            }
            if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL)) {
                element.setTransparent(true);
            }
        });

        getElementPortMap().forEach((portId, port) -> {
            var srcPort = getSrcPort(portId);
            port.setSrcPort(srcPort);
        });
    }

    @Override
    public void fromData(ScenePluginDataBO pluginData) {
        super.fromData(pluginData);
        loadRelated();
    }

    @Override
    public void initBase() {
        super.initBase();
        pluginModelHandler = new CloudScenePluginModelHandler(this);
        initiateHandler = new InitiateHandler(this);
        createHandler = new CreateHandler(this);
        deleteHandler = new DeleteHandler(this);
    }

    @Override
    public void reload() {
        setScene(null);
        getElementMap().clear();
        getElementPortMap().clear();

        pluginModelHandler.loadScene(pluginModelHandler.readScene());
        pluginModelHandler.loadElements(pluginModelHandler.readElements(getElementIds().toArray(new String[0])));
        pluginModelHandler.loadElementPorts(pluginModelHandler.readElementPorts(getElementPortIds()
                .toArray(new String[0])));
        loadRelated();
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
    public void initiate() {
        initiateHandler.initiate();
    }

    @Override
    public void create() {
        createHandler.create();
    }

    @Override
    public void delete() {
        deleteHandler.delete();
    }
}
