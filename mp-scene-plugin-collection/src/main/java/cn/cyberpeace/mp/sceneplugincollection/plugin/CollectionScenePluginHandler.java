package cn.cyberpeace.mp.sceneplugincollection.plugin;

import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.CreateHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.DeleteHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.ElementCreateHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.InitiateHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.PrepareHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.modelhandler.CollectionScenePluginModelHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.WazuhCommon;
import lombok.Data;
import org.springframework.stereotype.Component;



@Component
@PluginKey(value = PluginConstant.PLUGIN_KEY, priority = PluginConstant.PLUGIN_PRIORITY)
@Data
public class CollectionScenePluginHandler extends ScenePluginHandler<CollectionPluginSceneBO,
        CollectionPluginSceneElementBO, CollectionPluginSceneElementPortBO, Object> {

    private CollectionScenePluginModelHandler pluginModelHandler;

    private InitiateHandler initiateHandler;
    private CreateHandler createHandler;
    private DeleteHandler deleteHandler;
    private ElementCreateHandler elementCreateHandler ;
    private PrepareHandler prepareHandler;

    /**
     * correctData
     */
    @Override
    public void correctData() {
        if (getScene() == null) {
            setScene(ClassUtils.createInstance(getSceneBOClass()));
        }
    }

    /**
     * initBase
     */
    @Override
    public void initBase() {
        super.initBase();
        pluginModelHandler = new CollectionScenePluginModelHandler(this);
        initiateHandler = new InitiateHandler(this);
        createHandler = new CreateHandler(this);
        deleteHandler = new DeleteHandler(this);
        prepareHandler = new PrepareHandler(this);
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
     * prepare
     *
     * @param partialStart
     */
    @Override
    public void prepare(boolean partialStart) {
        prepareHandler.prepare(partialStart);
    }

    /**
     * initiate
     */
    @Override
    public void initiate() {
        initiateHandler.initiate();
    }

    /**
     * create
     */
    @Override
    public void create() {
        createHandler.create();
    }

    /**
     * delete
     */
    @Override
    public void delete() {
        deleteHandler.delete();
    }

    /**
     * createElement
     *
     * @param nodeId
     */
    @Override
    public void createElement(String nodeId) {
        CollectionPluginSceneElementBO element = getElement(nodeId);
        if (element == null) {
            return;
        }
        new ElementCreateHandler(this, getSceneHandler().getScene().getEvent()).createElement(element);
    }

    /**
     * snapshotElement
     *
     * @param nodeId
     * @param snapshotId
     * @param snapshotName
     * @param description
     */
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

    /**
     * snapshotElementV2
     *
     * @param nodeId
     * @param snapshotId
     * @param snapshotName
     * @param description
     */
    @Override
    public void snapshotElementV2(String nodeId, String snapshotId, String snapshotName, String description) {
        CollectionPluginSceneElementBO element = getElement(nodeId);
        if (element == null) {
            return;
        }
        if (Boolean.TRUE.equals(element.getEnableProbeCollect())) {
            WazuhCommon.uninstallAgent(element.getSrcElement().getEid(), element.getSrcElement().getSystemType());
        }
    }

    /**
     * initClass
     */
    @Override
    protected void initClass() {
        super.sceneBOClass = CollectionPluginSceneBO.class;
        super.elementBOClass = CollectionPluginSceneElementBO.class;
        super.elementPortBOClass = CollectionPluginSceneElementPortBO.class;
        super.contextClass = Object.class;
    }
}
