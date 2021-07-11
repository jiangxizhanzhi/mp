package cn.cyberpeace.mp.scene.core.scene;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.stereotype.BeanFieldClasses;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.base.util.thread.taskpool.DelayTaskPool;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.cyberpeace.mp.scene.core.base.BaseSceneHandler;
import cn.cyberpeace.mp.scene.core.bo.CollectedElementStatusBO;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneBO;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneStatusBO;
import cn.cyberpeace.mp.scene.core.bo.SceneAreaBO;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.ScenePieceBO;
import cn.cyberpeace.mp.scene.core.bo.SceneTopologyBO;
import cn.cyberpeace.mp.scene.core.event.SceneElementEventHandler;
import cn.cyberpeace.mp.scene.core.event.SceneElementEventTransition;
import cn.cyberpeace.mp.scene.core.event.SceneEventHandler;
import cn.cyberpeace.mp.scene.core.event.base.EventFlag;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.core.event.base.EventTransition;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginHandler;
import cn.cyberpeace.mp.scene.model.SceneAreaModel;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneElementPluginModel;
import cn.cyberpeace.mp.scene.model.SceneElementPortConnectionModel;
import cn.cyberpeace.mp.scene.model.SceneElementPortModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.model.ScenePieceModel;
import cn.cyberpeace.mp.scene.repository.SceneAreaMapper;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.scene.repository.SceneElementPluginMapper;
import cn.cyberpeace.mp.scene.repository.SceneElementPortConnectionMapper;
import cn.cyberpeace.mp.scene.repository.SceneElementPortMapper;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import cn.cyberpeace.mp.scene.repository.ScenePieceMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Slf4j
@ClassFieldClasses({
        SceneDataBO.class,
        SceneTopologyBO.class,
        SceneBO.class,
        SceneAreaBO.class,
        SceneElementBO.class,
        SceneElementPortBO.class,
        SceneElementPortConnectionBO.class,
        ScenePieceBO.class,
        SceneModel.class,
        SceneAreaModel.class,
        SceneElementModel.class,
        SceneElementPortModel.class,
        SceneElementPortConnectionModel.class,
        SceneElementPluginModel.class,
        ScenePieceModel.class,
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
        ScenePieceMapper.class,
})
public class SceneHandler extends BaseSceneHandler<
        SceneModel,
        SceneAreaModel,
        SceneElementModel,
        SceneElementPortModel,
        SceneElementPortConnectionModel,
        SceneElementPluginModel,
        ScenePieceModel,
        SceneBO,
        SceneAreaBO,
        SceneElementPortBO,
        SceneElementBO,
        SceneElementPortConnectionBO,
        ScenePieceBO,
        SceneTopologyBO,
        SceneDataBO,
        SceneMapper,
        SceneAreaMapper,
        SceneElementMapper,
        SceneElementPortMapper,
        SceneElementPortConnectionMapper,
        SceneElementPluginMapper,
        ScenePieceMapper,
        SceneContextBO,
        ScenePluginHandler<?, ?, ?, ?>,
        SceneHandler> {

    private static final DelayTaskPool delayTaskPool = new DelayTaskPool();

    
    private static Map<String, Integer> progressPercentageMap = new ConcurrentHashMap<>();

    @Setter
    private boolean realTimeUpdate;

    private final Map<String, SceneEventHandler> sceneEventHandlerMap = new ConcurrentHashMap<>();

    private final Map<String, SceneElementEventHandler> elementEventHandlerMap = new ConcurrentHashMap<>();

    private final List<SceneReporter> reporters = SceneReporter.getReporters();

    public final Set<String> excludeHandleNodes = new HashSet<>();

    public boolean isHandleNode(String nodeId) {
        return !excludeHandleNodes.contains(nodeId);
    }

    public void addExcludeHandleNode(String nodeId) {
        excludeHandleNodes.add(nodeId);
    }

    private final StatusReporter<CollectedSceneStatusBO> sceneStatusReporter = (StatusReporter) BeanCommon.getBean("sceneStatusReporter");

    private final StatusReporter<CollectedElementStatusBO> elementStatusReporter = (StatusReporter) BeanCommon.getBean("elementStatusReporter");

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
            if (isHandleNode(element.getId())) {
                prepareElementEvent(element, elementEvent);
            }
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
        this.getScene().setProgressPercentage(this.calculateProgressPercentage());
        if (isTriggerEventSuccess(flag)) {
            
            asyncAckSceneStatusChanged();
            asyncAckSceneChanged(false, false);
        }
        return flag;
    }

    private int startElementEvent(String nodeId, int event) {
        var flag = getElementEventHandler(nodeId, event).startEvent();
        if (isTriggerEventSuccess(flag)) {
            
            asyncAckSceneElementStatusChanged(nodeId);
            asyncAckElementChanged(nodeId, false, false);
        }
        return flag;
    }

    private int overSceneEvent(int event) {
        var flag = getSceneEventHandler(event).overEvent();
        this.getScene().setProgressPercentage(this.calculateProgressPercentage());
        if (isTriggerEventSuccess(flag)) {
            asyncAckSceneStatusChanged();
            asyncAckSceneChanged(false, false);
        }
        return flag;
    }

    private int overElementEvent(String nodeId, int event) {
        var flag = getElementEventHandler(nodeId, event).overEvent();
        if (isTriggerEventSuccess(flag)) {
            asyncAckSceneElementStatusChanged(nodeId);
            asyncAckElementChanged(nodeId, false, false);
        }
        return flag;
    }

    public int errorScene(int event, String errorMessage) {
        var flag = getSceneEventHandler(event).error(errorMessage);
        if (isTriggerEventSuccess(flag)) {
            asyncAckSceneStatusChanged();
            asyncAckSceneChanged(false, false);
            save();
        }
        return flag;
    }

    public int errorElement(String nodeId, int event, String errorMessage) {
        var flag = getElementEventHandler(nodeId, event).error(errorMessage);
        if (isTriggerEventSuccess(flag)) {
            asyncAckSceneElementStatusChanged(nodeId);
            asyncAckElementChanged(nodeId, false, true);
        }
        return flag;
    }

    private void startAllEvent(int sceneEvent, int elementEvent) {
        startSceneEvent(sceneEvent);
        for (var element : getElementMap().values()) {
            if (isHandleNode(element.getId())) {
                startElementEvent(element.getId(), elementEvent);
            }
        }
    }

    private void overAllEvent(int sceneEvent, int elementEvent) {
        for (var element : getElementMap().values()) {
            if (isHandleNode(element.getId())) {
                overElementEvent(element.getId(), elementEvent);
            }
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
        String message = null;
        if (collectedSceneBO.getScene() != null && collectedSceneBO.getScene().getProcessMessage() != null) {
            message = collectedSceneBO.getScene().getProcessMessage();
        }
        if (message != null && message.length() >= 1) {
            String newMessage = message.substring(0, 1).toUpperCase();
            if (message.length() > 1) {
                newMessage = newMessage + message.substring(1).toLowerCase();
            }
            collectedSceneBO.getScene().setProcessMessage(newMessage);
        }
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
        collectedSceneElementBO.setSceneId(getScene().getEid());
        
        var sceneElement = JsonCommon.parse(JsonCommon.stringify(getElement(nodeId)), SceneElementBO.class);
        if (CollectionUtils.isNotEmpty(sceneElement.getPorts())) {
            
            var ports = sceneElement.getPorts().stream()
                    .filter(p -> !Boolean.TRUE.equals(p.getIsAuxiliary()))
                    .collect(Collectors.toList());
            sceneElement.setPorts(ports);
        }
        if (StringUtils.isNotEmpty(sceneElement.getIcon2()) && !FsCommon.isFileServerOuterUrl(sceneElement.getIcon2())) {
            sceneElement.setIcon2(FsCommon.genHttpUrl(sceneElement.getIcon2()));
        }
        collectedSceneElementBO.setSceneElement(sceneElement);
        var sceneElementPorts = sceneElement.getPorts();
        String message = null;
        if (sceneElement.getProcessMessage() != null) {
            message = sceneElement.getProcessMessage();
        }
        if (message != null && message.length() >= 1) {
            String newMessage = message.substring(0, 1).toUpperCase();
            if (message.length() > 1) {
                newMessage = newMessage + message.substring(1).toLowerCase();
            }
            sceneElement.setProcessMessage(newMessage);
        }

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
        
        if (getElement(nodeId) == null || Boolean.TRUE.equals(getElement(nodeId).getIsAuxiliary())) {
            return;
        }
        var collectedSceneElement = getCollectedSceneElementBO(nodeId);
        reporters.forEach(reporter -> reportTaskPool.executeTask(() -> reporter.reportElement(collectedSceneElement)));
    }

    public void ackSceneChanged(boolean forceSave, boolean withPlugins) {
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneProcessSaveRealtime())) {
            forceSave = false;
        }
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            realTimeUpdate = false;
        }
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
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getElementProcessSaveRealtime())) {
            forceSave = false;
        }
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            realTimeUpdate = false;
        }
        if (forceSave || realTimeUpdate) {
            if (withPlugins) {
                updateSceneElementWithPlugins(nodeId);
            } else {
                updateSceneElement(nodeId);
            }
        }
        reportElementUpdated(nodeId);
    }

    public void asyncAckSceneStatusChanged() {




    }

    public void asyncAckSceneElementStatusChanged(String nodeId) {




    }

    public void asyncAckSceneChanged(boolean forceSave, boolean withPlugins) {
        delayTaskPool.execute(StringUtils.joinWith(":", getSceneId(), forceSave, withPlugins), () -> {
            ackSceneChanged(forceSave, withPlugins);
        });
    }

    public void asyncAckElementChanged(String nodeId, boolean forceSave, boolean withPlugins) {
        delayTaskPool.execute(StringUtils.joinWith(":", getSceneId(), nodeId, forceSave, withPlugins), () -> {
            ackElementChanged(nodeId, forceSave, withPlugins);
        });
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
            asyncAckSceneChanged(false, false);
        }
        return flag;
    }

    public int progressElementProcess(String nodeId, int event, String process) {
        var flag = getElementEventHandler(nodeId, event).progressProcess(process);
        if (isTriggerEventSuccess(flag)) {
            asyncAckElementChanged(nodeId, false, false);
        }
        return flag;
    }

    public int overSceneProcess(int event) {
        var flag = getSceneEventHandler(event).overProcess();
        if (isTriggerEventSuccess(flag)) {
            boolean forceSave = Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneProcessSaveRealtime());
            boolean withPlugins = forceSave;
            asyncAckSceneChanged(forceSave, withPlugins);
        }
        return flag;
    }

    public int overElementProcess(String nodeId, int event) {
        var flag = getElementEventHandler(nodeId, event).overProcess();
        this.getScene().setProgressPercentage(this.calculateProgressPercentage());
        if (isTriggerEventSuccess(flag)) {
            boolean forceSave = Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getElementProcessSaveRealtime());
            boolean withPlugins = forceSave;
            asyncAckElementChanged(nodeId, forceSave, withPlugins);
        }
        return flag;
    }

    public void reformTopo() {
        for (var plugin : getPlugins()) {
            plugin.reformTopo();
        }
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
            log.error("failed to initiate scene , sceneId is {} , exception is {}", getSceneId(), ExceptionUtils.getStackTrace(e));
            errorScene(SceneModel.Event.INITIATE, e.getMessage());
            return false;
        }
        overAllEvent(SceneModel.Event.INITIATE, SceneElementModel.Event.INITIATE);
        if (isSave) {
            save();
        }
        return true;
    }

    public boolean prepare(boolean partialStart) {
        for (var plugin : getPlugins()) {
            plugin.prepare(partialStart);
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
            log.error("failed to create scene , sceneId is {} , exception is {}", getSceneId(), ExceptionUtils.getStackTrace(e));
            log.error("failed to create scene", e);
            errorScene(SceneModel.Event.CREATE, e.getMessage());
            return false;
        }
        overAllEvent(SceneModel.Event.CREATE, SceneElementModel.Event.CREATE);
        for (var nodeId : getElementMap().keySet()) {
            if (!isHandleNode(nodeId)) {
                asyncAckElementChanged(nodeId, false, false);
            }
        }
        save();
        return true;
    }

    public void start() {
    }

    public void stop() {
    }

    public void restart() {
    }

    public boolean modify(SceneHandler originSceneHandler, List<String> updateElementIds) {
        prepareAllEvent(SceneModel.Event.MODIFY, SceneElementModel.Event.MODIFY);
        startAllEvent(SceneModel.Event.MODIFY, SceneElementModel.Event.MODIFY);

        originSceneHandler.prepareAllEvent(SceneModel.Event.MODIFY, SceneElementModel.Event.MODIFY);
        originSceneHandler.startAllEvent(SceneModel.Event.MODIFY, SceneElementModel.Event.MODIFY);

        try {
            for (var plugin : getPlugins()) {
                plugin.modify(originSceneHandler, updateElementIds);
            }
        } catch (Exception e) {
            log.error("failed to modify scene , sceneId is {} , exception is {}", getSceneId(), ExceptionUtils.getStackTrace(e), e);
            log.error("scene modify failed", e);
            errorScene(SceneModel.Event.MODIFY, e.getMessage());
            return false;
        }
        overAllEvent(SceneModel.Event.MODIFY, SceneElementModel.Event.MODIFY);
        save();
        return true;
    }

    public boolean pause() {
        for (var e : getElementMap().values()) {
            if (e.getEvent() == SceneElementModel.Event.DEFAULT) {
                this.addExcludeHandleNode(e.getId());
            } else {
                var validEvents = SceneElementEventTransition.SCENE_ELEMENT_EVENT_TRANSITION.getOrDefault(
                        EventTransition.genKey(e.getEvent(), e.getEventStatus()), new ArrayList<>()
                );
                if (!validEvents.contains(SceneElementModel.Event.PAUSE)) {
                    this.addExcludeHandleNode(e.getId());
                }
            }
        }
        prepareAllEvent(SceneModel.Event.PAUSE, SceneElementModel.Event.PAUSE);
        startAllEvent(SceneModel.Event.PAUSE, SceneElementModel.Event.PAUSE);
        try {
            for (var plugin : getPlugins()) {
                plugin.pause();
            }
        } catch (Exception e) {
            log.error("failed to pause scene , sceneId is {} , exception is {}", getSceneId(), ExceptionUtils.getStackTrace(e));
            errorScene(SceneModel.Event.PAUSE, e.getMessage());
            return false;
        }
        overAllEvent(SceneModel.Event.PAUSE, SceneElementModel.Event.PAUSE);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            save();
        }
        return true;
    }

    public boolean unpause() {
        for (var e : getElementMap().values()) {
            if (e.getEvent() == SceneElementModel.Event.DEFAULT) {
                this.addExcludeHandleNode(e.getId());
            } else {
                var validEvents = SceneElementEventTransition.SCENE_ELEMENT_EVENT_TRANSITION.getOrDefault(
                        EventTransition.genKey(e.getEvent(), e.getEventStatus()), new ArrayList<>()
                );
                if (!validEvents.contains(SceneElementModel.Event.UNPAUSE)) {
                    this.addExcludeHandleNode(e.getId());
                }
            }
        }
        prepareAllEvent(SceneModel.Event.UNPAUSE, SceneElementModel.Event.UNPAUSE);
        startAllEvent(SceneModel.Event.UNPAUSE, SceneElementModel.Event.UNPAUSE);
        try {
            for (var plugin : getPlugins()) {
                plugin.unpause();
            }
        } catch (Exception e) {
            log.error("failed to unpause scene , sceneId is {} , exception is {}", getSceneId(), ExceptionUtils.getStackTrace(e));
            errorScene(SceneModel.Event.UNPAUSE, e.getMessage());
            return false;
        }
        overAllEvent(SceneModel.Event.UNPAUSE, SceneElementModel.Event.UNPAUSE);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            save();
        }
        return true;
    }

    public boolean suspend() {
        prepareAllEvent(SceneModel.Event.SUSPEND, SceneElementModel.Event.SUSPEND);
        startAllEvent(SceneModel.Event.SUSPEND, SceneElementModel.Event.SUSPEND);
        try {
            for (var plugin : getPlugins()) {
                plugin.suspend();
            }
        } catch (Exception e) {
            log.error("failed to suspend scene , sceneId is {} , exception is {}", getSceneId(), ExceptionUtils.getStackTrace(e));
            errorScene(SceneModel.Event.SUSPEND, e.getMessage());
            return false;
        }
        overAllEvent(SceneModel.Event.SUSPEND, SceneElementModel.Event.SUSPEND);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            save();
        }
        return true;
    }

    public boolean resume() {
        prepareAllEvent(SceneModel.Event.RESUME, SceneElementModel.Event.RESUME);
        startAllEvent(SceneModel.Event.RESUME, SceneElementModel.Event.RESUME);
        try {
            for (var plugin : getPlugins()) {
                plugin.resume();
            }
        } catch (Exception e) {
            log.error("failed to resume scene , sceneId is {} , exception is {}", getSceneId(), ExceptionUtils.getStackTrace(e));
            errorScene(SceneModel.Event.RESUME, e.getMessage());
            return false;
        }
        overAllEvent(SceneModel.Event.RESUME, SceneElementModel.Event.RESUME);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            save();
        }
        return true;
    }

    public void snapshot() {
    }

    @Override
    public boolean delete() {
        prepareAllEvent(SceneModel.Event.DELETE, SceneElementModel.Event.DELETE);
        startAllEvent(SceneModel.Event.DELETE, SceneElementModel.Event.DELETE);
        try {
            for (int i = getPlugins().size() - 1; i >= 0; i--) {
                getPlugins().get(i).delete();
            }
        } catch (Exception e) {
            log.error("failed to delete scene , sceneId is {} , exception is {}", getSceneId(), ExceptionUtils.getStackTrace(e));
            errorScene(SceneModel.Event.DELETE, e.getMessage());
            return false;
        }
        overAllEvent(SceneModel.Event.DELETE, SceneElementModel.Event.DELETE);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            save();
        }
        return super.delete();
    }

    @Override
    public boolean logicDelete() {
        prepareAllEvent(SceneModel.Event.DELETE, SceneElementModel.Event.DELETE);
        startAllEvent(SceneModel.Event.DELETE, SceneElementModel.Event.DELETE);
        try {
            for (int i = getPlugins().size() - 1; i >= 0; i--) {
                getPlugins().get(i).delete();
            }
        } catch (Exception e) {
            log.error("failed to logicDelete scene , sceneId is {} , exception is {}", getSceneId(), ExceptionUtils.getStackTrace(e));
            errorScene(SceneModel.Event.DELETE, e.getMessage());
            return false;
        }
        overAllEvent(SceneModel.Event.DELETE, SceneElementModel.Event.DELETE);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            save();
        }
        return super.logicDelete();
    }

    
    public void modify() {
    }

    public void initiateElement(String nodeId) {
        SceneElementBO element = getElement(nodeId);
        prepareElementEvent(element, SceneElementModel.Event.INITIATE);
        startElementEvent(nodeId, SceneElementModel.Event.INITIATE);
        try {
            for (ScenePluginHandler plugin : getPlugins()) {
                plugin.initiateElement(nodeId);
            }
        } catch (Exception e) {
            log.error("failed to initiateElement , sceneId is {} , nodeId is {}, exception is {}", getSceneId(), nodeId, ExceptionUtils.getStackTrace(e));
            errorElement(nodeId, SceneElementModel.Event.INITIATE, e.getMessage());
            return;
        }
        overElementEvent(nodeId, SceneElementModel.Event.INITIATE);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            updateSceneElementWithPlugins(nodeId);
        }
    }

    public void createElement(String nodeId) {
        SceneElementBO element = getElement(nodeId);
        prepareElementEvent(element, SceneElementModel.Event.CREATE);
        startElementEvent(nodeId, SceneElementModel.Event.CREATE);
        try {
            for (ScenePluginHandler plugin : getPlugins()) {
                plugin.createElement(nodeId);
            }
        } catch (Exception e) {
            log.error("failed to createElement , sceneId is {} , nodeId is {}, exception is {}", getSceneId(), nodeId, ExceptionUtils.getStackTrace(e));
            log.error("failed to createElement", e);
            errorElement(nodeId, SceneElementModel.Event.CREATE, e.getMessage());
            return;
        }
        overElementEvent(nodeId, SceneElementModel.Event.CREATE);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            updateSceneElementWithPlugins(nodeId);
        }
    }

    public void stopElement(String nodeId) {
        SceneElementBO element = getElement(nodeId);
        prepareElementEvent(element, SceneElementModel.Event.STOP);
        startElementEvent(nodeId, SceneElementModel.Event.STOP);
        try {
            for (ScenePluginHandler plugin : getPlugins()) {
                plugin.stopElement(nodeId);
            }
        } catch (Exception e) {
            log.error("failed to stopElement , sceneId is {} , nodeId is {}, exception is {}", getSceneId(), nodeId, ExceptionUtils.getStackTrace(e));
            errorElement(nodeId, SceneElementModel.Event.STOP, e.getMessage());
            return;
        }
        overElementEvent(nodeId, SceneElementModel.Event.STOP);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            updateSceneElementWithPlugins(nodeId);
        }
    }

    public void startElement(String nodeId) {
        
        SceneElementBO element = getElement(nodeId);
        
        prepareElementEvent(element, SceneElementModel.Event.START);
        
        startElementEvent(nodeId, SceneElementModel.Event.START);
        try {
            for (ScenePluginHandler plugin : getPlugins()) {
                plugin.startElement(nodeId);
            }
        } catch (Exception e) {
            log.error("failed to startElement , sceneId is {} , nodeId is {}, exception is {}", getSceneId(), nodeId, ExceptionUtils.getStackTrace(e));
            errorElement(nodeId, SceneElementModel.Event.START, e.getMessage());
            return;
        }
        
        overElementEvent(nodeId, SceneElementModel.Event.START);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            updateSceneElementWithPlugins(nodeId);
        }
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
            log.error("failed to restartElement , sceneId is {} , nodeId is {}, exception is {}", getSceneId(), nodeId, ExceptionUtils.getStackTrace(e));
            errorElement(nodeId, SceneElementModel.Event.RESTART, e.getMessage());
            return;
        }
        overElementEvent(nodeId, SceneElementModel.Event.RESTART);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            updateSceneElementWithPlugins(nodeId);
        }
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
            log.error("failed to pauseElement , sceneId is {} , nodeId is {}, exception is {}", getSceneId(), nodeId, ExceptionUtils.getStackTrace(e));
            errorElement(nodeId, SceneElementModel.Event.PAUSE, e.getMessage());
            return;
        }
        overElementEvent(nodeId, SceneElementModel.Event.PAUSE);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            updateSceneElementWithPlugins(nodeId);
        }
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
            log.error("failed to unpauseElement , sceneId is {} , nodeId is {}, exception is {}", getSceneId(), nodeId, ExceptionUtils.getStackTrace(e));
            errorElement(nodeId, SceneElementModel.Event.UNPAUSE, e.getMessage());
            return;
        }
        overElementEvent(nodeId, SceneElementModel.Event.UNPAUSE);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            updateSceneElementWithPlugins(nodeId);
        }
    }

    public void snapshotElement(String nodeId) {
    }

    public void deleteElement(String nodeId) {
        SceneElementBO element = getElement(nodeId);
        prepareElementEvent(element, SceneElementModel.Event.DELETE);
        startElementEvent(nodeId, SceneElementModel.Event.DELETE);
        try {
            for (ScenePluginHandler plugin : getPlugins()) {
                plugin.deleteElement(nodeId);
            }
        } catch (Exception e) {
            log.error("failed to deleteElement , sceneId is {} , nodeId is {}, exception is {}", getSceneId(), nodeId, ExceptionUtils.getStackTrace(e));
            errorElement(nodeId, SceneElementModel.Event.DELETE, e.getMessage());
            return;
        }
        overElementEvent(nodeId, SceneElementModel.Event.DELETE);
        
        var scene = toData();
        Iterator<SceneElementBO> elementIts = scene.getTopology().getElements().iterator();
        while (elementIts.hasNext()) {
            var s = elementIts.next();
            if (s.getId().equals(nodeId)) {
                elementIts.remove();
                break;
            }
        }
        Set<String> deletePortIds = new HashSet<>();
        for (var p : element.getPorts()) {
            deletePortIds.add(p.getId());
        }
        Iterator<SceneElementPortConnectionBO> connectIts = scene.getTopology().getConnections().iterator();
        while (connectIts.hasNext()) {
            var c = connectIts.next();
            if (deletePortIds.contains(c.getSource()) || deletePortIds.contains(c.getTarget())) {
                connectIts.remove();
            }
        }
        for (var plugin : scene.getPlugins().values()) {
            plugin.getElements().remove(element.getId());
            for (var port : element.getPorts()) {
                plugin.getElementPorts().remove(port.getId());
            }
        }
        new SceneHandler(scene, null).save();
    }

    public void modifyElement(String nodeId, SceneHandler originHandler) {
        var element = getElement(nodeId);
        if (element.getEvent() != SceneElementModel.Event.STOP) {
            prepareElementEvent(element, SceneElementModel.Event.MODIFY);
            startElementEvent(nodeId, SceneElementModel.Event.MODIFY);
            try {
                
                for (ScenePluginHandler plugin : getPlugins()) {
                    plugin.modifyElement(nodeId, originHandler);
                }
            } catch (Exception e) {
                log.error("failed to modifyElement , sceneId is {} , nodeId is {}, exception is {}", getSceneId(),
                        nodeId, ExceptionUtils.getStackTrace(e));
                errorElement(nodeId, SceneElementModel.Event.MODIFY, e.getMessage());
                return;
            }
            overElementEvent(nodeId, SceneElementModel.Event.MODIFY);
            updateSceneElementWithPlugins(nodeId);
        } else {
            
            updateSceneElement(nodeId);
            asyncAckElementChanged(nodeId, false, false);
        }
    }

    public void recreateElement(String nodeId,String image) {
        SceneElementBO element = getElement(nodeId);
        prepareElementEvent(element, SceneElementModel.Event.RECREATE);
        startElementEvent(nodeId, SceneElementModel.Event.RECREATE);
        try {
            for (ScenePluginHandler plugin : getPlugins()) {
                plugin.recreateElement(nodeId,image);
            }
        } catch (Exception e) {
            log.error("failed to recreateElement , sceneId is {} , nodeId is {}, exception is {}", getSceneId(),
                    nodeId, ExceptionUtils.getStackTrace(e));
            errorElement(nodeId, SceneElementModel.Event.RECREATE, e.getMessage());
            return;
        }
        overElementEvent(nodeId, SceneElementModel.Event.RECREATE);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            updateSceneElementWithPlugins(nodeId);
        }
    }

    public void suspendElement(String nodeId) {
        SceneElementBO element = getElement(nodeId);
        prepareElementEvent(element, SceneElementModel.Event.SUSPEND);
        startElementEvent(nodeId, SceneElementModel.Event.SUSPEND);
        try {
            for (ScenePluginHandler plugin : getPlugins()) {
                plugin.suspendElement(nodeId);
            }
        } catch (Exception e) {
            log.error("failed to suspendElement , sceneId is {} , nodeId is {}, exception is {}", getSceneId(),
                    nodeId, ExceptionUtils.getStackTrace(e));
            errorElement(nodeId, SceneElementModel.Event.SUSPEND, e.getMessage());
            return;
        }
        overElementEvent(nodeId, SceneElementModel.Event.SUSPEND);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            updateSceneElementWithPlugins(nodeId);
        }
    }

    public void resumeElement(String nodeId) {
        SceneElementBO element = getElement(nodeId);
        prepareElementEvent(element, SceneElementModel.Event.RESUME);
        startElementEvent(nodeId, SceneElementModel.Event.RESUME);
        try {
            for (ScenePluginHandler plugin : getPlugins()) {
                plugin.resumeElement(nodeId);
            }
        } catch (Exception e) {
            log.error("failed to resumeElement , sceneId is {} , nodeId is {}, exception is {}", getSceneId(), nodeId
                    , ExceptionUtils.getStackTrace(e));
            errorElement(nodeId, SceneElementModel.Event.RESUME, e.getMessage());
            return;
        }
        overElementEvent(nodeId, SceneElementModel.Event.RESUME);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            updateSceneElementWithPlugins(nodeId);
        }
    }

    public void snapshotElement(String nodeId, String snapshotId, String snapshotName, String description) {
        SceneElementBO element = getElement(nodeId);
        prepareElementEvent(element, SceneElementModel.Event.SNAPSHOT);
        startElementEvent(nodeId, SceneElementModel.Event.SNAPSHOT);
        try {
            for (int i = getPlugins().size() - 1; i >= 0; i--) {
                getPlugins().get(i).snapshotElement(nodeId, snapshotId, snapshotName, description);
            }
        } catch (Exception e) {
            log.error("failed to snapshotElement , sceneId is {} , nodeId is {}, exception is {}", getSceneId(),
                    nodeId, ExceptionUtils.getStackTrace(e));
            errorElement(nodeId, SceneElementModel.Event.SNAPSHOT, e.getMessage());
            return;
        }
        overElementEvent(nodeId, SceneElementModel.Event.SNAPSHOT);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            updateSceneElementWithPlugins(nodeId);
        }
    }

    @Override
    public SceneDataBO toData() {
        SceneDataBO sceneDataBO = super.toData();
        sceneDataBO.getTopology().getScene().setProgressPercentage(this.calculateProgressPercentage());
        return sceneDataBO;
    }

    public synchronized int addProgressPercentage(int newProcess) {
        Integer progressPercentage = progressPercentageMap.getOrDefault(getSceneId(), 0);
        int result = progressPercentage + newProcess;
        if (result >= 100) {
            progressPercentageMap.put(getSceneId(), 95);
            return 95;
        } else {
            progressPercentageMap.put(getSceneId(), result);
            return result;
        }
    }

    private Integer calculateProgressPercentage() {
        if (CollectionUtils.isEmpty(this.getElementMap().values())) {
            progressPercentageMap.remove(getSceneId());
            return 100;
        } else {
            if (this.isTerminateEvent(getScene().getEvent()) && getScene().getEventStatus() == EventStatus.OVER) {
                progressPercentageMap.remove(getSceneId());
                return 100;
            } else {
                var percentage = progressPercentageMap.get(getSceneId());
                if (percentage == null) {
                    return 5;
                } else {
                    return percentage;
                }
            }
        }
    }

    private boolean isTerminateEvent(int state) {
        Set<Integer> terminateStates = new HashSet<>() {{
            this.add(SceneModel.Event.CREATE);
            this.add(SceneModel.Event.START);
            this.add(SceneModel.Event.STOP);
            this.add(SceneModel.Event.RESTART);
            this.add(SceneModel.Event.PAUSE);
            this.add(SceneModel.Event.UNPAUSE);
            this.add(SceneModel.Event.SNAPSHOT);
            this.add(SceneModel.Event.DELETE);
            this.add(SceneModel.Event.MODIFY);
            this.add(SceneModel.Event.SUSPEND);
            this.add(SceneModel.Event.RESUME);
        }};
        return terminateStates.contains(state);
    }

    private CollectedSceneStatusBO getCollectedSceneStatusBO() {
        CollectedSceneStatusBO bo = new CollectedSceneStatusBO();
        bo.setName(getScene().getName());
        bo.setEvent(getScene().getEvent());
        bo.setEventStatus(getScene().getEventStatus());
        bo.setProcess(getScene().getProcess());
        bo.setProcessStatus(getScene().getProcessStatus());
        bo.setProcessMessage(getScene().getProcessMessage());
        bo.setProgressPercentage(this.calculateProgressPercentage());
        return bo;
    }

    private CollectedElementStatusBO getCollectedElementStatusBO(String nodeId) {
        CollectedElementStatusBO bo = new CollectedElementStatusBO();
        SceneElementBO element = getElement(nodeId);
        bo.setName(getScene().getName());
        bo.setNodeId(nodeId);
        bo.setEvent(element.getEvent());
        bo.setEventStatus(element.getEventStatus());
        bo.setProcess(element.getProcess());
        bo.setProcessStatus(element.getProcessStatus());
        bo.setProcessMessage(element.getProcessMessage());
        return bo;
    }
}
