package cn.cyberpeace.mp.scene.core.scene;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.thread.taskpool.DelayTaskPool;
import cn.cyberpeace.mp.scene.constant.CommonSceneStage;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
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
import cn.cyberpeace.mp.scene.core.bo.ScenePieceBO;
import cn.cyberpeace.mp.scene.core.bo.SceneTopologyBO;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.event.SceneElementEventHandler;
import cn.cyberpeace.mp.scene.core.event.SceneElementEventTransition;
import cn.cyberpeace.mp.scene.core.event.SceneEventHandler;
import cn.cyberpeace.mp.scene.core.event.SceneStageRecorder;
import cn.cyberpeace.mp.scene.core.event.base.EventFlag;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.core.event.base.EventTransition;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementPortBO;
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
import org.springframework.beans.BeanUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 场景实例的处理
 * 场景实例的功能由本类对外提供
 */
@Slf4j
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
        SceneContextBO,
        ScenePluginHandler<?, ?, ?, ?>,
        SceneHandler> {

    private static final DelayTaskPool delayTaskPool = new DelayTaskPool();

    @Setter
    private boolean realTimeUpdate;

    private final Map<String, SceneEventHandler> sceneEventHandlerMap = new ConcurrentHashMap<>();

    private final Map<String, SceneElementEventHandler> elementEventHandlerMap = new ConcurrentHashMap<>();

    private final List<SceneReporter> reporters = SceneReporter.getReporters();

    private SceneStageRecorder sceneStageRecorder;

    public void setSceneStageRecorder(SceneStageRecorder sceneStageRecorder) {
        this.sceneStageRecorder = sceneStageRecorder;
    }

    public SceneStageRecorder getSceneStageRecorder() {
        return this.sceneStageRecorder;
    }

    public final Set<String> excludeHandleNodes = new HashSet<>();

    public boolean isHandleNode(String nodeId) {
        return !excludeHandleNodes.contains(nodeId);
    }

    public void addExcludeHandleNode(String nodeId) {
        excludeHandleNodes.add(nodeId);
    }

    private final SceneReportTaskPool reportTaskPool = SceneReportTaskPool.getInstance();

    public int errorScene(int event, String errorMessage) {
        int flag = getSceneEventHandler(event).error(errorMessage);
        if (isTriggerEventSuccess(flag)) {
            asyncAckSceneChanged(false, false);
            SceneStageRecorder.getRecorder(getSceneId()).refreshSceneBO(this.getScene());
            SceneStageRecorder.removeRecorder(getSceneId());
            String message = this.getScene().getProcessMessage();
            for (SceneElementBO e : this.getElementMap().values()) {
                if (e.getEventStatus() == 5) {
                    message += ";" + e.getProcessMessage();
                }
            }
            if (StringUtils.isNotEmpty(message) && message.length() > 2000) {
                message = message.substring(0, 2000);
            }
            this.getScene().setProcessMessage(message);
            save();
        }
        return flag;
    }

    public int errorElement(String nodeId, int event, String errorMessage) {
        int flag = getElementEventHandler(nodeId, event).error(errorMessage);
        if (isTriggerEventSuccess(flag)) {
            asyncAckElementChanged(nodeId, false, true);
        }
        return flag;
    }

    public SceneHandler() {
    }

    public SceneHandler(SceneDataBO sceneDataBO, SceneContextBO context) {
        init(sceneDataBO, false, context);
        this.setSceneStageRecorder(new SceneStageRecorder(this.getScene()));
    }

    public SceneHandler(SceneDataBO sceneDataBO, boolean isAreaMode, SceneContextBO context) {
        init(sceneDataBO, isAreaMode, context);
        this.setSceneStageRecorder(new SceneStageRecorder(this.getScene()));
    }

    public SceneHandler(String sceneId, String areaId, SceneContextBO context) {
        init(sceneId, areaId, false, context);
        this.setSceneStageRecorder(new SceneStageRecorder(this.getScene()));
    }

    public SceneHandler(String sceneId, String areaId, boolean isAreaMode, SceneContextBO context) {
        init(sceneId, areaId, isAreaMode, context);
        this.setSceneStageRecorder(new SceneStageRecorder(this.getScene()));
    }

    @Override
    protected void initMapper() {
        super.sceneMapper = BeanCommon.getBean(SceneMapper.class);
        super.sceneAreaMapper = BeanCommon.getBean(SceneAreaMapper.class);
        super.sceneElementMapper = BeanCommon.getBean(SceneElementMapper.class);
        super.sceneElementPortMapper = BeanCommon.getBean(SceneElementPortMapper.class);
        super.sceneElementPortConnectionMapper = BeanCommon.getBean(SceneElementPortConnectionMapper.class);
        super.sceneElementPluginMapper = BeanCommon.getBean(SceneElementPluginMapper.class);
        super.scenePieceMapper = BeanCommon.getBean(ScenePieceMapper.class);
    }

    @Override
    protected void initClass() {
        super.sceneDataBOClass = SceneDataBO.class;
        super.sceneTopologyBOClass = SceneTopologyBO.class;
        super.sceneBOClass = SceneBO.class;
        super.sceneAreaBOClass = SceneAreaBO.class;
        super.sceneElementBOClass = SceneElementBO.class;
        super.sceneElementPortBOClass = SceneElementPortBO.class;
        super.sceneElementPortConnectionBOClass = SceneElementPortConnectionBO.class;
        super.scenePieceBOClass = ScenePieceBO.class;
        super.sceneModelClass = SceneModel.class;
        super.sceneAreaModelClass = SceneAreaModel.class;
        super.sceneElementModelClass = SceneElementModel.class;
        super.sceneElementPortModelClass = SceneElementPortModel.class;
        super.sceneElementPortConnectionModelClass = SceneElementPortConnectionModel.class;
        super.sceneElementPluginModelClass = SceneElementPluginModel.class;
        super.scenePieceModelClass = ScenePieceModel.class;
        super.sceneHandlerClass = SceneHandler.class;

        Field field = ClassUtils.getField(this.getClass(), "scenePluginHandlerInterface", true, false);
        ClassUtils.setFieldValue(field, this, ScenePluginHandler.class);
    }

    @Override
    public void beforeInitPlugin(ScenePluginHandler<?, ?, ?, ?> plugin) {
        plugin.setSceneHandler(this);
    }

    public CollectedSceneBO getCollectedSceneBO() {
        CollectedSceneBO collectedSceneBO = new CollectedSceneBO();
        SceneBO sceneBO = new SceneBO();
        BeanUtils.copyProperties(getScene(), sceneBO);
        collectedSceneBO.setScene(sceneBO);

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

        Locale locale = LocaleContextHolder.getLocale();
        boolean chinaFlag = StringUtils.isBlank(locale.getLanguage()) || Locale.CHINA.getLanguage().equals(locale.getLanguage());
        if (chinaFlag) {
            collectedSceneBO.getScene().setStageName(String.format("正在%s", collectedSceneBO.getScene().getStageName()));
        }

        Map<String, Object> pluginData = new HashMap<>();
        getPlugins().forEach(plugin -> {
            BasePluginSceneBO<?, ? extends BasePluginSceneBO<?, ?, SceneBO>, SceneBO> pluginScene = plugin.getScene();
            if (pluginScene != null) {
                pluginData.put(plugin.getKey(), plugin.getScene());
            }
        });
        collectedSceneBO.setScenePluginData(pluginData);
        return collectedSceneBO;
    }

    public CollectedSceneElementBO getCollectedSceneElementBO(String nodeId) {
        CollectedSceneElementBO collectedSceneElementBO = new CollectedSceneElementBO();
        collectedSceneElementBO.setSceneId(getScene().getEid());
        // 深拷贝一个
        SceneElementBO sceneElement = JsonCommon.parse(JsonCommon.stringify(getElement(nodeId)), SceneElementBO.class);
        if (CollectionUtils.isNotEmpty(sceneElement.getPorts())) {
            // 所有非辅助端口
            List<SceneElementPortBO> ports = new ArrayList<>();
            for (SceneElementPortBO p : sceneElement.getPorts()) {
                if (!Boolean.TRUE.equals(p.getIsAuxiliary())) {
                    ports.add(p);
                }
            }
            sceneElement.setPorts(ports);
        }
        if (StringUtils.isNotEmpty(sceneElement.getIcon2()) && !FsCommon.isFileServerOuterUrl(sceneElement.getIcon2())) {
            sceneElement.setIcon2(FsCommon.genHttpUrl(sceneElement.getIcon2()));
        }
        collectedSceneElementBO.setSceneElement(sceneElement);
        List<SceneElementPortBO> sceneElementPorts = sceneElement.getPorts();
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
            BasePluginSceneElementBO<?, ? extends BasePluginSceneElementBO<?, ?, SceneElementBO>, SceneElementBO> pluginElement = plugin.getElement(nodeId);
            if (pluginElement != null) {
                pluginData.put(plugin.getKey(), pluginElement);
            }

            if (sceneElementPorts != null) {
                Map<String, Object> pluginPorts = new HashMap<>();
                sceneElementPorts.forEach(sceneElementPort -> {
                    String portId = sceneElementPort.getId();
                    BasePluginSceneElementPortBO<?, ? extends BasePluginSceneElementPortBO<?, ?, SceneElementPortBO>, SceneElementPortBO> pluginPort = plugin.getPort(portId);
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
        CollectedSceneBO collectedScene = getCollectedSceneBO();
        reporters.forEach(reporter -> reportTaskPool.executeTask(() -> reporter.reportScene(collectedScene)));
    }

    public void reportElementUpdated(String nodeId) {
        // 辅助节点无需通知
        if (getElement(nodeId) == null || Boolean.TRUE.equals(getElement(nodeId).getIsAuxiliary())) {
            return;
        }
        CollectedSceneElementBO collectedSceneElement = getCollectedSceneElementBO(nodeId);
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
        getSceneStageRecorder().progress(process);
        int flag = getSceneEventHandler(event).progressProcess(process);
        if (isTriggerEventSuccess(flag)) {
            asyncAckSceneChanged(false, false);
        }
        return flag;
    }

    public int progressElementProcess(String nodeId, int event, String process) {
        int flag = getElementEventHandler(nodeId, event).progressProcess(process);
        if (isTriggerEventSuccess(flag)) {
            asyncAckElementChanged(nodeId, false, false);
        }
        return flag;
    }

    public int overSceneProcess(int event) {
        getSceneStageRecorder().overProgress(this.getScene().getProcess());
        int flag = getSceneEventHandler(event).overProcess();
        if (isTriggerEventSuccess(flag)) {
            boolean forceSave = Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneProcessSaveRealtime());
            boolean withPlugins = forceSave;
            asyncAckSceneChanged(forceSave, withPlugins);
        }
        return flag;
    }

    public int overElementProcess(String nodeId, int event) {
        int flag = getElementEventHandler(nodeId, event).overProcess();
        this.getScene().setProgressPercentage(this.calculateProgressPercentage());
        if (isTriggerEventSuccess(flag)) {
            boolean forceSave = Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getElementProcessSaveRealtime());
            boolean withPlugins = forceSave;
            asyncAckElementChanged(nodeId, forceSave, withPlugins);
        }
        return flag;
    }

    public void reformTopo() {
        for (ScenePluginHandler<?, ?, ?, ?> plugin : getPlugins()) {
            plugin.reformTopo();
        }
    }

    public boolean initiate() {
        return initiate(true);
    }

    public boolean initiate(boolean isSave) {
        getSceneStageRecorder().startStage(CommonSceneStage.INIT);
        prepareAllEvent(SceneModel.Event.INITIATE, SceneElementModel.Event.INITIATE);
        startAllEvent(SceneModel.Event.INITIATE, SceneElementModel.Event.INITIATE);
        try {
            for (ScenePluginHandler<?, ?, ?, ?> plugin : getPlugins()) {
                plugin.initiate();
            }
        } catch (Exception e) {
            log.error("failed to initiate scene , sceneId is {} , exception is {}", getSceneId(), ExceptionUtils.getStackTrace(e));
            errorScene(SceneModel.Event.INITIATE, e.getMessage());
            return false;
        }
        getSceneStageRecorder().endStage(CommonSceneStage.INIT);
        overAllEvent(SceneModel.Event.INITIATE, SceneElementModel.Event.INITIATE);
        if (isSave) {
            save();
        }
        return true;
    }

    public boolean prepare(boolean partialStart) {
        for (ScenePluginHandler<?, ?, ?, ?> plugin : getPlugins()) {
            plugin.prepare(partialStart);
        }
        return true;
    }

    public boolean create() {
        prepareAllEvent(SceneModel.Event.CREATE, SceneElementModel.Event.CREATE);
        startAllEvent(SceneModel.Event.CREATE, SceneElementModel.Event.CREATE);
        try {
            for (ScenePluginHandler<?, ?, ?, ?> plugin : getPlugins()) {
                plugin.create();
            }
        } catch (Exception e) {
            log.error("failed to create scene , sceneId is {} , exception is {}", getSceneId(), ExceptionUtils.getStackTrace(e));
            log.error("failed to create scene", e);
            errorScene(SceneModel.Event.CREATE, e.getMessage());
            return false;
        }
        overAllEvent(SceneModel.Event.CREATE, SceneElementModel.Event.CREATE);
        for (String nodeId : getElementMap().keySet()) {
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
            for (ScenePluginHandler<?, ?, ?, ?> plugin : getPlugins()) {
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
        for (SceneElementBO e : getElementMap().values()) {
            if (e.getEvent() == SceneElementModel.Event.DEFAULT) {
                this.addExcludeHandleNode(e.getId());
            } else {
                List<Integer> validEvents = SceneElementEventTransition.SCENE_ELEMENT_EVENT_TRANSITION.getOrDefault(
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
            for (ScenePluginHandler<?, ?, ?, ?> plugin : getPlugins()) {
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
        for (SceneElementBO e : getElementMap().values()) {
            if (e.getEvent() == SceneElementModel.Event.DEFAULT) {
                this.addExcludeHandleNode(e.getId());
            } else {
                List<Integer> validEvents = SceneElementEventTransition.SCENE_ELEMENT_EVENT_TRANSITION.getOrDefault(
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
            for (ScenePluginHandler<?, ?, ?, ?> plugin : getPlugins()) {
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
            for (ScenePluginHandler<?, ?, ?, ?> plugin : getPlugins()) {
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
            for (ScenePluginHandler<?, ?, ?, ?> plugin : getPlugins()) {
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

    /**
     * TODO: 修改场景
     */
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
        //获取节点的网元数据
        SceneElementBO element = getElement(nodeId);
        //节点前置事件
        prepareElementEvent(element, SceneElementModel.Event.START);
        //开始节点事件
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
        //节点后置事件
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
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
        // 去除 被删除节点 的数据
        SceneDataBO scene = toData();
        Iterator<SceneElementBO> elementIts = scene.getTopology().getElements().iterator();
        while (elementIts.hasNext()) {
            SceneElementBO s = elementIts.next();
            if (s.getId().equals(nodeId)) {
                elementIts.remove();
                break;
            }
        }
        Set<String> deletePortIds = new HashSet<>();
        for (SceneElementPortBO p : element.getPorts()) {
            deletePortIds.add(p.getId());
        }
        Iterator<SceneElementPortConnectionBO> connectIts = scene.getTopology().getConnections().iterator();
        while (connectIts.hasNext()) {
            SceneElementPortConnectionBO c = connectIts.next();
            if (deletePortIds.contains(c.getSource()) || deletePortIds.contains(c.getTarget())) {
                connectIts.remove();
            }
        }
        for (ScenePluginDataBO plugin : scene.getPlugins().values()) {
            plugin.getElements().remove(element.getId());
            for (SceneElementPortBO port : element.getPorts()) {
                plugin.getElementPorts().remove(port.getId());
            }
        }
        new SceneHandler(scene, null).save();
    }

    public void modifyElement(String nodeId, SceneHandler originHandler) {
        SceneElementBO element = getElement(nodeId);
        if (element.getEvent() != SceneElementModel.Event.STOP) {
            prepareElementEvent(element, SceneElementModel.Event.MODIFY);
            startElementEvent(nodeId, SceneElementModel.Event.MODIFY);
            try {
                // 插件数据 modify
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
            // 只更新拓扑信息，但是不更新插件信息
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

    public void deleteElementSnapshot(String nodeId, String snapshotId) {
        SceneElementBO element = getElement(nodeId);
        prepareElementEvent(element, SceneElementModel.Event.SNAPSHOT);
        startElementEvent(nodeId, SceneElementModel.Event.SNAPSHOT);
        try {
            for (ScenePluginHandler plugin : getPlugins()) {
                plugin.deleteElementSnapshot(nodeId, snapshotId);
            }
        } catch (Exception e) {
            errorElement(nodeId, SceneElementModel.Event.SNAPSHOT, e.getMessage());
            return;
        }
        overElementEvent(nodeId, SceneElementModel.Event.SNAPSHOT);
        if (!Boolean.TRUE.equals(SceneSingleton.sceneAppConfig.getSceneHandlerRealtimeUpdate())) {
            updateSceneElementWithPlugins(nodeId);
        }
    }

    public void elementSnapshotAction(String nodeId, String snapshotId, String action) {
        SceneElementBO element = getElement(nodeId);
        prepareElementEvent(element, SceneElementModel.Event.SNAPSHOT);
        startElementEvent(nodeId, SceneElementModel.Event.SNAPSHOT);
        try {
            for (ScenePluginHandler plugin : getPlugins()) {
                plugin.elementSnapshotAction(nodeId, snapshotId, action);
            }
        } catch (Exception e) {
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
        Integer progressPercentage = this.getScene().getProgressPercentage();
        int result = progressPercentage + newProcess;
        if (result >= 100) {
            this.getScene().setProgressPercentage(progressPercentage);
            return 95;
        } else {
            this.getScene().setProgressPercentage(progressPercentage);
            return result;
        }
    }

    private Integer calculateProgressPercentage() {
        if (this.isTerminateEvent(getScene().getEvent()) && getScene().getEventStatus() == EventStatus.OVER) {
            return 100;
        }
        BigDecimal total = BigDecimal.valueOf(0);
        BigDecimal current = BigDecimal.valueOf(0);
        if (CollectionUtils.isNotEmpty(this.getScene().getStageList())) {
            for (SceneStageRecorder.SceneStageRecord stage : this.getScene().getStageList()) {
                total = total.add(BigDecimal.valueOf(100));
                current = current.add(BigDecimal.valueOf(stage.getStagePercentage()));
            }
        }
        if (current.intValue() == 0) {
            return 0;
        }
        return current.divide(total, 4, RoundingMode.DOWN).multiply(BigDecimal.valueOf(100)).intValue();
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
        for (SceneElementBO element : getElementMap().values()) {
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
        int flag = getSceneEventHandler(event).startEvent();
        this.getScene().setProgressPercentage(this.calculateProgressPercentage());
        if (isTriggerEventSuccess(flag)) {
            // 推送场景最新的事件状态
            asyncAckSceneChanged(false, false);
        }
        return flag;
    }

    private int startElementEvent(String nodeId, int event) {
        int flag = getElementEventHandler(nodeId, event).startEvent();
        if (isTriggerEventSuccess(flag)) {
            // 推送节点最新的事件状态
            asyncAckElementChanged(nodeId, false, false);
        }
        return flag;
    }

    private int overSceneEvent(int event) {
        int flag = getSceneEventHandler(event).overEvent();
        this.getScene().setProgressPercentage(this.calculateProgressPercentage());
        if (isTriggerEventSuccess(flag)) {
            asyncAckSceneChanged(false, false);
        }
        return flag;
    }

    private int overElementEvent(String nodeId, int event) {
        int flag = getElementEventHandler(nodeId, event).overEvent();
        if (isTriggerEventSuccess(flag)) {
            asyncAckElementChanged(nodeId, false, false);
        }
        return flag;
    }

    private void startAllEvent(int sceneEvent, int elementEvent) {
        startSceneEvent(sceneEvent);
        for (SceneElementBO element : getElementMap().values()) {
            if (isHandleNode(element.getId())) {
                startElementEvent(element.getId(), elementEvent);
            }
        }
    }

    private void overAllEvent(int sceneEvent, int elementEvent) {
        for (SceneElementBO element : getElementMap().values()) {
            if (isHandleNode(element.getId())) {
                overElementEvent(element.getId(), elementEvent);
            }
        }
        overSceneEvent(sceneEvent);
    }
}
