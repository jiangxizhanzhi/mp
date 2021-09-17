package cn.cyberpeace.mp.scene.core.scene;

import cn.cyberpeace.mp.base.annotation.BeanFieldClasses;
import cn.cyberpeace.mp.base.annotation.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.base.BaseSceneHandler;
import cn.cyberpeace.mp.scene.core.bo.*;
import cn.cyberpeace.mp.scene.core.event.SceneElementEventHandler;
import cn.cyberpeace.mp.scene.core.event.SceneEventHandler;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginContext;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginHandler;
import cn.cyberpeace.mp.scene.model.*;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPortModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneNodeModel;
import cn.cyberpeace.mp.scene.repository.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 场景实例的处理
 * 场景实例的功能由本类对外提供
 */
@Slf4j
@ClassFieldClasses({
        SceneDataBO.class,
        SceneTopologyBO.class,
        SceneBO.class,
        SceneAreaBO.class,
        SceneElementBO.class,
        SceneElementPortBO.class,
        SceneElementPortConnectionBO.class,
        SceneModel.class,
        SceneAreaModel.class,
        SceneElementModel.class,
        SceneElementPortModel.class,
        SceneElementPortConnectionModel.class,
        SceneElementPluginModel.class,
        ScenePluginHandler.class,
        SceneHandler.class,
})
@BeanFieldClasses({
        SceneMapper.class,
        SceneAreaMapper.class,
        SceneElementMapper.class,
        SceneElementPortMapper.class,
        SceneElementPortConnectionMapper.class,
        SceneElementPluginMapper.class,
})
public class SceneHandler extends BaseSceneHandler<
        SceneModel,
        SceneAreaModel,
        SceneElementModel,
        SceneElementPortModel,
        SceneElementPortConnectionModel,
        SceneElementPluginModel,
        SceneBO,
        SceneAreaBO,
        SceneElementPortBO,
        SceneElementBO,
        SceneElementPortConnectionBO,
        SceneTopologyBO,
        SceneDataBO,
        SceneMapper,
        SceneAreaMapper,
        SceneElementMapper,
        SceneElementPortMapper,
        SceneElementPortConnectionMapper,
        SceneElementPluginMapper,
        ScenePluginHandler<?, ?, ?>,
        SceneHandler> {

    private final Map<String, SceneEventHandler> sceneEventHandlerMap = new HashMap<>();

    private final Map<String, SceneElementEventHandler> elementEventHandlerMap = new HashMap<>();

    public CollectedSceneBO getCollectedSceneBO() {
        var collectedSceneBO = new CollectedSceneBO();
        collectedSceneBO.setScene(getScene());
        Map<String, Object> pluginData = new HashMap<>();
        getPluginMap().forEach((pluginKey, plugin) -> {
            var pluginScene = plugin.getScene();
            if (pluginScene != null) {
                pluginData.put(pluginKey, plugin.getScene());
            }
        });
        collectedSceneBO.setScenePluginData(pluginData);
        return collectedSceneBO;
    }

    public CollectedSceneElementBO getCollectedSceneElementBO(String nodeId) {
        var collectedSceneElementBO = new CollectedSceneElementBO();
        var sceneElement = getElement(nodeId);
        collectedSceneElementBO.setSceneElement(sceneElement);
        var sceneElementPorts = sceneElement.getPorts();
        
        Map<String, Object> pluginData = new HashMap<>();
        Map<String, Map<String, Object>> portPluginData = new HashMap<>();
        getPluginMap().forEach((pluginKey, plugin) -> {
            var pluginElement = plugin.getElement(nodeId);
            if (pluginElement != null) {
                pluginData.put(pluginKey, pluginElement);
            }
            
            if (sceneElementPorts != null) {
                Map<String, Object> pluginPorts = new HashMap<>();
                sceneElementPorts.forEach(sceneElementPort -> {
                    var portId = sceneElementPort.getId();
                    var pluginPort = plugin.getPort(portId);
                    if (pluginPort != null) {
                        pluginPorts.put(portId, pluginPort);
                    }
                });
                portPluginData.put(pluginKey, pluginPorts);
            }
        });
        return collectedSceneElementBO;
    }

    public void notifySceneUpdated() {
        var collectedScene = getCollectedSceneBO();
        // TODO 通知
    }

    public void notifyElementUpdated(String nodeId) {
        var collectedSceneElement = getCollectedSceneElementBO(nodeId);
        // TODO 通知
    }

    private String genEventKey(String targetId, int event) {
        return String.format("%s:%d", targetId, event);
    }

    private void prepareSceneEvent(int event) {
        sceneEventHandlerMap.put(genEventKey(getScene().getEid(), event), new SceneEventHandler(getScene(), event));
    }

    private void prepareElementEvent(SceneElementBO element, int event) {
        elementEventHandlerMap.put(genEventKey(element.getId(), event), new SceneElementEventHandler(element, event));
    }

    private void prepareAllEvent(int sceneEvent, int elementEvent) {
        prepareSceneEvent(sceneEvent);
        for (var element : getElementMap().values()) {
            prepareElementEvent(element, elementEvent);
        }
    }

    private SceneEventHandler getSceneEventHandler(int event) {
        return sceneEventHandlerMap.get(genEventKey(getScene().getEid(), event));
    }

    private SceneElementEventHandler getElementEventHandler(String nodeId, int event) {
        return elementEventHandlerMap.get(genEventKey(nodeId, event));
    }

    private void startSceneEvent(int event) {
        getSceneEventHandler(event).startEvent();
    }

    private void startElementEvent(String nodeId, int event) {
        getElementEventHandler(nodeId, event).startEvent();
    }

    private void overSceneEvent(int event) {
        getSceneEventHandler(event).overEvent();
    }

    private void overElementEvent(String nodeId, int event) {
        getElementEventHandler(nodeId, event).overEvent();
    }

    private void startAllEvent(int sceneEvent, int elementEvent) {
        startSceneEvent(sceneEvent);
        for (var element : getElementMap().values()) {
            startElementEvent(element.getId(), elementEvent);
        }
    }

    private void overAllEvent(int sceneEvent, int elementEvent) {
        overSceneEvent(sceneEvent);
        for (var element : getElementMap().values()) {
            overElementEvent(element.getId(), elementEvent);
        }
    }

    public SceneHandler() {}

    public SceneHandler(SceneDataBO sceneDataBO) {
        init(sceneDataBO, false);
    }

    public SceneHandler(SceneDataBO sceneDataBO, boolean isAreaMode) {
        init(sceneDataBO, isAreaMode);
    }

    public SceneHandler(String sceneId, String areaId) {
        init(sceneId, areaId, false);
    }

    public SceneHandler(String sceneId, String areaId, boolean isAreaMode) {
        init(sceneId, areaId, isAreaMode);
    }

    @Override
    public void initPluginContext(String pluginKey, ScenePluginHandler<?, ?, ?> plugin) {
        var context = new ScenePluginContext();
        context.setSceneHandler(this);
        plugin.setContext(context);
    }

    public void progressSceneProcess(int event, String process, String processMessage) {
        getSceneEventHandler(event).progressProcess(process, processMessage);
    }

    public void progressElementProcess(String nodeId, int event, String process, String processMessage) {
        getElementEventHandler(nodeId, event).progressProcess(process, processMessage);
    }

    public void overSceneProcess(int event, String processMessage) {
        getSceneEventHandler(event).overProcess(processMessage);
    }

    public void overElementProcess(String nodeId, int event, String processMessage) {
        getElementEventHandler(nodeId, event).overProcess(processMessage);
    }

    public void errorSceneProcess(int event, String processMessage) {
        getSceneEventHandler(event).error(processMessage);
    }

    public void errorElementProcess(String nodeId, int event, String processMessage) {
        getElementEventHandler(nodeId, event).error(processMessage);
    }

    public void initiate() {
        prepareAllEvent(SceneModel.Event.INITIATE, SceneElementModel.Event.INITIATE);
        startAllEvent(SceneModel.Event.INITIATE, SceneElementModel.Event.INITIATE);
        try {
            for (var plugin : getPluginMap().values()) {
                plugin.initiate();
            }
        } catch (Exception e) {
            errorSceneProcess(SceneModel.Event.INITIATE, e.getMessage());
            return;
        }
        overAllEvent(SceneModel.Event.INITIATE, SceneElementModel.Event.INITIATE);
    }

    public void create() {
        prepareAllEvent(SceneModel.Event.CREATE, SceneElementModel.Event.CREATE);
        startAllEvent(SceneModel.Event.CREATE, SceneElementModel.Event.CREATE);
        try {
            for (var plugin : getPluginMap().values()) {
                plugin.create();
            }
        } catch (Exception e) {
            errorSceneProcess(SceneModel.Event.INITIATE, e.getMessage());
            return;
        }
        overAllEvent(SceneModel.Event.CREATE, SceneElementModel.Event.CREATE);
    }

    public void start() {
        getPluginMap().forEach((key, plugin) -> {
            plugin.start();
        });
    }

    public void stop() {
        getPluginMap().forEach((key, plugin) -> {
            plugin.stop();
        });
    }

    public void restart() {
        getPluginMap().forEach((key, plugin) -> {
            plugin.restart();
        });
    }

    public void pause() {
        getPluginMap().forEach((key, plugin) -> {
            plugin.pause();
        });
    }

    public void unpause() {
        getPluginMap().forEach((key, plugin) -> {
            plugin.unpause();
        });
    }

    public void snapshot() {
        getPluginMap().forEach((key, plugin) -> {
            plugin.snapshot();
        });
    }

    @Override
    public void delete() {
        prepareAllEvent(SceneModel.Event.DELETE, SceneElementModel.Event.DELETE);
        startAllEvent(SceneModel.Event.DELETE, SceneElementModel.Event.DELETE);
        try {
            for (var plugin : getPluginMap().values()) {
                plugin.delete();
            }
        } catch (Exception e) {
            errorSceneProcess(SceneModel.Event.DELETE, e.getMessage());
            return;
        }
        overAllEvent(SceneModel.Event.DELETE, SceneElementModel.Event.DELETE);

        super.delete();
    }

    @Override
    public boolean logicDelete() {
        prepareAllEvent(SceneModel.Event.DELETE, SceneElementModel.Event.DELETE);
        startAllEvent(SceneModel.Event.DELETE, SceneElementModel.Event.DELETE);
        try {
            for (var plugin : getPluginMap().values()) {
                plugin.delete();
            }
        } catch (Exception e) {
            errorSceneProcess(SceneModel.Event.DELETE, e.getMessage());
            return false;
        }
        overAllEvent(SceneModel.Event.DELETE, SceneElementModel.Event.DELETE);

        return super.logicDelete();
    }

    /**
     * TODO: 修改场景
     */
    public void modify() {
        getPluginMap().forEach((key, plugin) -> {
            plugin.modify();
        });
    }
}
