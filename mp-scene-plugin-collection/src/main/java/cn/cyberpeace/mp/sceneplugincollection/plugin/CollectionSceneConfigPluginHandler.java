package cn.cyberpeace.mp.sceneplugincollection.plugin;

import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.plugin.SceneConfigPluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneConfigBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.plugin.modelhandler.CollectionSceneConfigPluginModelHandler;
import org.springframework.stereotype.Component;


@ClassFieldClasses({
        CollectionPluginSceneConfigBO.class,
        CollectionPluginSceneConfigElementBO.class,
        CollectionPluginSceneConfigElementPortBO.class,
        Object.class,
})
@Component
@PluginKey(value = PluginConstant.PLUGIN_KEY, priority = PluginConstant.PLUGIN_PRIORITY)
public class CollectionSceneConfigPluginHandler extends SceneConfigPluginHandler<CollectionPluginSceneConfigBO,
        CollectionPluginSceneConfigElementBO, CollectionPluginSceneConfigElementPortBO, Object> {

    private CollectionSceneConfigPluginModelHandler pluginModelHandler;

    private static final String EVENT = "1";
    private static final String LOG = "2";
    private static final String REGISTRY_PROBE_ID = "registry";
    private static final String FILE_PROBE_ID = "file";

    @Override
    public void correctData() {
        if (getElementMap() != null && !getElementMap().isEmpty()) {
            getElementMap().values().forEach(e -> {
                if (e.getProbes() == null || e.getProbes().isEmpty()) {
                    return;
                }
                e.getProbes().forEach(probe -> {
                    if (probe.getScriptUrl() != null) {
                        probe.setExecType(Probe.ExecType.SCRIPT_PROBE);
                    } else if (REGISTRY_PROBE_ID.equals(probe.getId())) {
                        probe.setExecType(Probe.ExecType.REGISTRY_PROBE);
                    } else if (FILE_PROBE_ID.equals(probe.getId())) {
                        probe.setExecType(Probe.ExecType.FILE_PROBE);
                    } else if (probe.getCommon() && LOG.equals(probe.getType())) {
                        probe.setExecType(Probe.ExecType.LOG_PROBE);
                    } else {
                        probe.setExecType(Probe.ExecType.BASE_PROBE);
                    }
                });
            });
        }
    }

    @Override
    public void initBase() {
        super.initBase();
        pluginModelHandler = new CollectionSceneConfigPluginModelHandler(this);
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

}
