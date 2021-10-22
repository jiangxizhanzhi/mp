package cn.cyberpeace.mp.scene.core.scene;

import cn.cyberpeace.mp.base.annotation.BeanFieldClasses;
import cn.cyberpeace.mp.base.annotation.ClassFieldClasses;
import cn.cyberpeace.mp.scene.constant.ElementLifeCycleEnum;
import cn.cyberpeace.mp.scene.core.base.BaseSceneHandler;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneBO;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneAreaBO;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.SceneTopologyBO;
import cn.cyberpeace.mp.scene.core.event.SceneElementEventHandler;
import cn.cyberpeace.mp.scene.core.event.SceneEventHandler;
import cn.cyberpeace.mp.scene.core.event.base.EventFlag;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginHandler;
import cn.cyberpeace.mp.scene.model.SceneAreaModel;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneElementPluginModel;
import cn.cyberpeace.mp.scene.model.SceneElementPortConnectionModel;
import cn.cyberpeace.mp.scene.model.SceneElementPortModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.SceneAreaMapper;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.scene.repository.SceneElementPluginMapper;
import cn.cyberpeace.mp.scene.repository.SceneElementPortConnectionMapper;
import cn.cyberpeace.mp.scene.repository.SceneElementPortMapper;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
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
        SceneContextBO,
        ScenePluginHandler<?, ?, ?, ?>,
        SceneHandler> {

    @Setter
    private boolean realTimeUpdate;

    private final Map<String, SceneEventHandler> sceneEventHandlerMap = new HashMap<>();

    private final Map<String, SceneElementEventHandler> elementEventHandlerMap = new HashMap<>();

    private final List<SceneReporter> reporters = SceneReporter.getReporters();

    private final SceneReportTaskPool reportTaskPool = SceneReportTaskPool.getInstance();

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

    private boolean isTriggerEventSuccess(int flag) {
        return EventFlag.SUCCESS == flag;
    }

    private int startSceneEvent(int event) {
        var flag = getSceneEventHandler(event).startEvent();
        if (isTriggerEventSuccess(flag)) {
            if (realTimeUpdate) {
                updateScene();
            }
            reportSceneUpdated();
        }
        return flag;
    }

    private int startElementEvent(String nodeId, int event) {
        var flag = getElementEventHandler(nodeId, event).startEvent();
        if (isTriggerEventSuccess(flag)) {
            if (realTimeUpdate) {
                updateSceneElement(nodeId);
            }
            reportElementUpdated(nodeId);
        }
        return flag;
    }

    private int overSceneEvent(int event) {
        var flag = getSceneEventHandler(event).overEvent();
        if (isTriggerEventSuccess(flag)) {
            if (realTimeUpdate) {
                updateScene();
            }
            reportSceneUpdated();
        }
        return flag;
    }

    private int overElementEvent(String nodeId, int event) {
        var flag = getElementEventHandler(nodeId, event).overEvent();
        if (isTriggerEventSuccess(flag)) {
            if (realTimeUpdate) {
                updateSceneElement(nodeId);
            }
            reportElementUpdated(nodeId);
        }
        return flag;
    }

    public int errorScene(int event, String errorMessage) {
        var flag = getSceneEventHandler(event).error(errorMessage);
        if (isTriggerEventSuccess(flag)) {
            save();
            reportSceneUpdated();
        }
        return flag;
    }

    public int errorElement(String nodeId, int event, String errorMessage) {
        var flag = getElementEventHandler(nodeId, event).error(errorMessage);
        if (isTriggerEventSuccess(flag)) {
            if (realTimeUpdate) {
                updateSceneElementWithPlugins(nodeId);
            }
            reportElementUpdated(nodeId);
        }
        return flag;
    }

    private void startAllEvent(int sceneEvent, int elementEvent) {
        startSceneEvent(sceneEvent);
        for (var element : getElementMap().values()) {
            startElementEvent(element.getId(), elementEvent);
        }
    }

    private void overAllEvent(int sceneEvent, int elementEvent) {
        for (var element : getElementMap().values()) {
            overElementEvent(element.getId(), elementEvent);
        }
        overSceneEvent(sceneEvent);
    }

    public SceneHandler() {
    }

    public SceneHandler(SceneDataBO sceneDataBO, SceneContextBO context) {
        init(sceneDataBO, false, context);
    }

    public SceneHandler(SceneDataBO sceneDataBO, boolean isAreaMode, SceneContextBO context) {
        init(sceneDataBO, isAreaMode, context);
    }

    public SceneHandler(String sceneId, String areaId, SceneContextBO context) {
        init(sceneId, areaId, false, context);
    }

    public SceneHandler(String sceneId, String areaId, boolean isAreaMode, SceneContextBO context) {
        init(sceneId, areaId, isAreaMode, context);
    }

    @Override
    public void beforeInitPlugin(ScenePluginHandler<?, ?, ?, ?> plugin) {
        plugin.setSceneHandler(this);
    }

    public CollectedSceneBO getCollectedSceneBO() {
        var collectedSceneBO = new CollectedSceneBO();
        collectedSceneBO.setScene(getScene());
        Map<String, Object> pluginData = new HashMap<>();
        getPlugins().forEach(plugin -> {
            var pluginScene = plugin.getScene();
            if (pluginScene != null) {
                pluginData.put(plugin.getKey(), plugin.getScene());
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
        getPlugins().forEach(plugin -> {
            var pluginElement = plugin.getElement(nodeId);
            if (pluginElement != null) {
                pluginData.put(plugin.getKey(), pluginElement);
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
                portPluginData.put(plugin.getKey(), pluginPorts);
            }
        });
        collectedSceneElementBO.setSceneElementPluginData(pluginData);
        collectedSceneElementBO.setSceneElementPortPluginData(portPluginData);
        return collectedSceneElementBO;
    }

    public void reportSceneUpdated() {
        var collectedScene = getCollectedSceneBO();
        reporters.forEach(reporter -> reportTaskPool.executeTask(() -> reporter.reportScene(collectedScene)));
    }

    public void reportElementUpdated(String nodeId) {
        var collectedSceneElement = getCollectedSceneElementBO(nodeId);
        reporters.forEach(reporter -> reportTaskPool.executeTask(() -> reporter.reportElement(collectedSceneElement)));
    }

    public void prepareSceneProcess(int event, List<EventProcess> eventProcesses) {
        getSceneEventHandler(event).addProcess(eventProcesses);
    }

    public void prepareElementProcess(String nodeId, int event, List<EventProcess> eventProcesses) {
        getElementEventHandler(nodeId, event).addProcess(eventProcesses);
    }

    public int progressSceneProcess(int event, String process) {
        var flag = getSceneEventHandler(event).progressProcess(process);
        if (isTriggerEventSuccess(flag)) {
            if (realTimeUpdate) {
                updateScene();
            }
            reportSceneUpdated();
        }
        return flag;
    }

    public int progressElementProcess(String nodeId, int event, String process) {
        var flag = getElementEventHandler(nodeId, event).progressProcess(process);
        if (isTriggerEventSuccess(flag)) {
            if (realTimeUpdate) {
                updateSceneElement(nodeId);
            }
            reportElementUpdated(nodeId);
        }
        return flag;
    }

    public int overSceneProcess(int event) {
        var flag = getSceneEventHandler(event).overProcess();
        if (isTriggerEventSuccess(flag)) {
            if (realTimeUpdate) {
                updateSceneWithPlugins();
            }
            reportSceneUpdated();
        }
        return flag;
    }

    public int overElementProcess(String nodeId, int event) {
        var flag = getElementEventHandler(nodeId, event).overProcess();
        if (isTriggerEventSuccess(flag)) {
            if (realTimeUpdate) {
                updateSceneElementWithPlugins(nodeId);
            }
            reportElementUpdated(nodeId);
        }
        return flag;
    }

    public boolean initiate() {
        return initiate(true);
    }

    public boolean initiate(boolean isSave) {
        prepareAllEvent(SceneModel.Event.INITIATE, SceneElementModel.Event.INITIATE);
        startAllEvent(SceneModel.Event.INITIATE, SceneElementModel.Event.INITIATE);
        try {
            for (var plugin : getPlugins()) {
                plugin.initiate();
            }
        } catch (Exception e) {
            errorScene(SceneModel.Event.INITIATE, e.getMessage());
            return false;
        }
        overAllEvent(SceneModel.Event.INITIATE, SceneElementModel.Event.INITIATE);
        if (isSave) {
            save();
        }
        return true;
    }

    public boolean create() {
        prepareAllEvent(SceneModel.Event.CREATE, SceneElementModel.Event.CREATE);
        startAllEvent(SceneModel.Event.CREATE, SceneElementModel.Event.CREATE);
        try {
            for (var plugin : getPlugins()) {
                plugin.create();
            }
        } catch (Exception e) {
            errorScene(SceneModel.Event.CREATE, e.getMessage());
            return false;
        }
        overAllEvent(SceneModel.Event.CREATE, SceneElementModel.Event.CREATE);
        save();
        return true;
    }

    public void start() {
    }

    public void stop() {
    }

    public void restart() {
    }

    public void pause() {
    }

    public void unpause() {
    }

    public void snapshot() {
    }

    public boolean elementLifeCycleControl(String nodeId, ElementLifeCycleEnum lifeCycle) {
        prepareElementEvent(getElement(nodeId), lifeCycle.getEvent());
        startElementEvent(nodeId, lifeCycle.getEvent());
        try {
            for (ScenePluginHandler pluginHandler : getPlugins()) {
                pluginHandler.elementLifeCycle(nodeId, lifeCycle);
            }
        } catch (Exception e) {
            log.error("failed to {} element,nodeId:{}", lifeCycle.getAction(), nodeId, e);
            return false;
        }
        overElementEvent(nodeId, lifeCycle.getEvent());
        return true;
    }

    @Override
    public boolean delete() {
        prepareAllEvent(SceneModel.Event.DELETE, SceneElementModel.Event.DELETE);
        startAllEvent(SceneModel.Event.DELETE, SceneElementModel.Event.DELETE);
        try {
            for (var plugin : getPlugins()) {
                plugin.delete();
            }
        } catch (Exception e) {
            errorScene(SceneModel.Event.DELETE, e.getMessage());
            return false;
        }
        overAllEvent(SceneModel.Event.DELETE, SceneElementModel.Event.DELETE);

        return super.delete();
    }

    @Override
    public boolean logicDelete() {
        prepareAllEvent(SceneModel.Event.DELETE, SceneElementModel.Event.DELETE);
        startAllEvent(SceneModel.Event.DELETE, SceneElementModel.Event.DELETE);
        try {
            for (var plugin : getPlugins()) {
                plugin.delete();
            }
        } catch (Exception e) {
            errorScene(SceneModel.Event.DELETE, e.getMessage());
            return false;
        }
        overAllEvent(SceneModel.Event.DELETE, SceneElementModel.Event.DELETE);

        return super.logicDelete();
    }

    /**
     * TODO: 修改场景
     */
    public void modify() {
    }
}
