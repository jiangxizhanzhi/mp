package cn.cyberpeace.mp.sceneplugincollection.plugin;

import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.plugin.azure.lifecyclehandler.AzureCreateHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.azure.lifecyclehandler.AzureDeleteHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.*;
import cn.cyberpeace.mp.sceneplugincollection.plugin.modelhandler.CollectionScenePluginModelHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.WazuhCommon;
import lombok.Data;
import org.springframework.stereotype.Component;


@ClassFieldClasses({
        CollectionPluginSceneBO.class,
        CollectionPluginSceneElementBO.class,
        CollectionPluginSceneElementPortBO.class,
        Object.class,
})
@Component
@PluginKey(value = PluginConstant.PLUGIN_KEY, priority = PluginConstant.PLUGIN_PRIORITY)
@Data
public class CollectionScenePluginHandler extends ScenePluginHandler<CollectionPluginSceneBO,
        CollectionPluginSceneElementBO, CollectionPluginSceneElementPortBO, Object> {

    private CollectionScenePluginModelHandler pluginModelHandler;

    private InitiateHandler initiateHandler;
    private CreateHandler createHandler;
    private DeleteHandler deleteHandler;
    private ElementCreateHandler elementCreateHandler;
    private PrepareHandler prepareHandler;

    @Override
    public void correctData() {
        if (getScene() == null) {
            setScene(ClassUtils.createInstance(getSceneBOClass()));
        }
    }

    @Override
    public void initBase() {
        super.initBase();
        Integer cloudType = getSceneHandler().getScene().getCloudType();
        pluginModelHandler = new CollectionScenePluginModelHandler(this);
        initiateHandler = new InitiateHandler(this);

        if (cloudType == null || cloudType == BaseSceneModel.CloudType.DEFAULT) {
            createHandler = new CreateHandler(this);
            deleteHandler = new DeleteHandler(this);
        } else {
            createHandler = new AzureCreateHandler(cloudType, this);
            deleteHandler = new AzureDeleteHandler(cloudType,this);
        }


        prepareHandler = new PrepareHandler(this);
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
    public void prepare(boolean partialStart) {
        prepareHandler.prepare(partialStart);
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

    @Override
    public void createElement(String nodeId) {
        CollectionPluginSceneElementBO element = getElement(nodeId);
        if (element == null) {
            return;
        }
        new ElementCreateHandler(this, getSceneHandler().getScene().getEvent()).createElement(element);
    }

    @Override
    public void snapshotElement(String nodeId, String snapshotId, String snapshotName, String description) {
        CollectionPluginSceneElementBO element = getElement(nodeId);
        if (element == null) {
            return;
        }
        if (Boolean.TRUE.equals(element.getEnableProbeCollect())) {
            WazuhCommon.uninstallAgent(element.getSrcElement().getEid(), element.getSrcElement().getSystemType());
        }
    }
}
