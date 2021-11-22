package cn.cyberpeace.mp.scene.core.scene;

import cn.cyberpeace.mp.base.common.stereotype.BeanFieldClasses;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
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
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
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
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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

    // mock的进度
    private static Map<String, Integer> progressPercentageMap = new ConcurrentHashMap<>();

    @Setter
    private boolean realTimeUpdate;

    private final Map<String, SceneEventHandler> sceneEventHandlerMap = new ConcurrentHashMap<>();

    private final Map<String, SceneElementEventHandler> elementEventHandlerMap = new ConcurrentHashMap<>();

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
        this.getScene().setProgressPercentage(this.calculateProgressPercentage());
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

    public void ackSceneChanged(boolean forceSave) {
        ackSceneChanged(forceSave, true);
    }

    public void ackElementChanged(String nodeId, boolean forceSave) {
        ackElementChanged(nodeId, forceSave, true);
    }

    public void ackSceneChanged(boolean forceSave, boolean withPlugins) {
        if (forceSave || realTimeUpdate) {
            if (withPlugins) {
                updateSceneWithPlugins();
            } else {
                updateScene();
            }
        }
        reportSceneUpdated();
    }

    public void ackElementChanged(String nodeId, boolean forceSave, boolean withPlugins) {
        if (forceSave || realTimeUpdate) {
            if (withPlugins) {
                updateSceneElementWithPlugins(nodeId);
            } else {
                updateSceneElement(nodeId);
            }
        }
        reportElementUpdated(nodeId);
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
            ackSceneChanged(false, false);
        }
        return flag;
    }

    public int progressElementProcess(String nodeId, int event, String process) {
        var flag = getElementEventHandler(nodeId, event).progressProcess(process);
        if (isTriggerEventSuccess(flag)) {
            ackElementChanged(nodeId, false, false);
        }
        return flag;
    }

    public int overSceneProcess(int event) {
        var flag = getSceneEventHandler(event).overProcess();
        if (isTriggerEventSuccess(flag)) {
            ackSceneChanged(true, true);
        }
        return flag;
    }

    public int overElementProcess(String nodeId, int event) {
        var flag = getElementEventHandler(nodeId, event).overProcess();
        this.getScene().setProgressPercentage(this.calculateProgressPercentage());
        if (isTriggerEventSuccess(flag)) {
            ackElementChanged(nodeId, true, true);
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

    public boolean pause() {
        prepareAllEvent(SceneModel.Event.PAUSE, SceneElementModel.Event.PAUSE);
        startAllEvent(SceneModel.Event.PAUSE, SceneElementModel.Event.PAUSE);
        try {
            for (var plugin : getPlugins()) {
                plugin.pause();
            }
        } catch (Exception e) {
            errorScene(SceneModel.Event.PAUSE, e.getMessage());
            return false;
        }
        overAllEvent(SceneModel.Event.PAUSE, SceneElementModel.Event.PAUSE);
        return true;
    }

    public boolean unpause() {
        prepareAllEvent(SceneModel.Event.UNPAUSE, SceneElementModel.Event.UNPAUSE);
        startAllEvent(SceneModel.Event.UNPAUSE, SceneElementModel.Event.UNPAUSE);
        try {
            for (var plugin : getPlugins()) {
                plugin.unpause();
            }
        } catch (Exception e) {
            errorScene(SceneModel.Event.UNPAUSE, e.getMessage());
            return false;
        }
        overAllEvent(SceneModel.Event.UNPAUSE, SceneElementModel.Event.UNPAUSE);
        return true;
    }

    public void snapshot() {
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

    public void startElement(String nodeId) {
    }

    public void stopElement(String nodeId) {
    }

    public void restartElement(String nodeId) {
        SceneElementBO element = getElement(nodeId);
        prepareElementEvent(element, SceneElementModel.Event.RESTART);
        startElementEvent(nodeId, SceneElementModel.Event.RESTART);
        try {
            for (ScenePluginHandler plugin : getPlugins()) {
                plugin.restartElement(nodeId);
            }
        } catch (Exception e) {
            errorElement(nodeId, SceneElementModel.Event.RESTART, e.getMessage());
            return;
        }
        overElementEvent(nodeId, SceneElementModel.Event.RESTART);
    }

    public void pauseElement(String nodeId) {
        SceneElementBO element = getElement(nodeId);
        prepareElementEvent(element, SceneElementModel.Event.PAUSE);
        startElementEvent(nodeId, SceneElementModel.Event.PAUSE);
        try {
            for (ScenePluginHandler plugin : getPlugins()) {
                plugin.pauseElement(nodeId);
            }
        } catch (Exception e) {
            errorElement(nodeId, SceneElementModel.Event.PAUSE, e.getMessage());
            return;
        }
        overElementEvent(nodeId, SceneElementModel.Event.PAUSE);
    }

    public void unpauseElement(String nodeId) {
        SceneElementBO element = getElement(nodeId);
        prepareElementEvent(element, SceneElementModel.Event.UNPAUSE);
        startElementEvent(nodeId, SceneElementModel.Event.UNPAUSE);
        try {
            for (ScenePluginHandler plugin : getPlugins()) {
                plugin.unpauseElement(nodeId);
            }
        } catch (Exception e) {
            errorElement(nodeId, SceneElementModel.Event.UNPAUSE, e.getMessage());
            return;
        }
        overElementEvent(nodeId, SceneElementModel.Event.UNPAUSE);
    }

    public void snapshotElement(String nodeId) {
    }

    public void deleteElement(String nodeId) {
    }

    public void modifyElement(String nodeId) {
    }

    public void recreateElement(String nodeId) {
        SceneElementBO element = getElement(nodeId);
        prepareElementEvent(element, SceneElementModel.Event.RECREATE);
        startElementEvent(nodeId, SceneElementModel.Event.RECREATE);
        try {
            for (ScenePluginHandler plugin : getPlugins()) {
                plugin.recreateElement(nodeId);
            }
        } catch (Exception e) {
            errorElement(nodeId, SceneElementModel.Event.RECREATE, e.getMessage());
            return;
        }
        overElementEvent(nodeId, SceneElementModel.Event.RECREATE);
    }

    @Override
    public SceneDataBO toData() {
        SceneDataBO sceneDataBO = super.toData();
        sceneDataBO.getTopology().getScene().setProgressPercentage(this.calculateProgressPercentage());
        return sceneDataBO;
    }

    // TODO
    private Integer calculateProgressPercentage() {
        if (CollectionUtils.isEmpty(this.getElementMap().values())) {
            progressPercentageMap.remove(getSceneId());
            return 100;
        } else {
            if (getScene().getEventStatus() == EventStatus.OVER) {
                progressPercentageMap.remove(getSceneId());
                return 100;
            }
            // mock 一个进度
            int newProcess = Math.abs(new Random().nextInt(10));
            return this.addProgressPercentage(newProcess);
        }
    }

    private synchronized int addProgressPercentage(int newProcess) {
        Integer progressPercentage = progressPercentageMap.getOrDefault(getSceneId(), 0);
        int result = progressPercentage + newProcess;
        if (result >= 100) {
            progressPercentageMap.put(getSceneId(), 99);
            return 99;
        } else {
            progressPercentageMap.put(getSceneId(), result);
            return result;
        }
    }
}
