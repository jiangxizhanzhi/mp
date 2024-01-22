package cn.cyberpeace.mp.scene.core.event.base;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.scene.constant.SceneErrorCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件处理
 * @param <TEventModelInterface> 事件目标模型类
 * @param <TEventModel> 事件模型类
 *           1.不做特殊处理
 *           2.当前最新事件状态存储到缓存（TODO）
 *           3.事件每一个状态变化都持久化（暂不实现）
 */
@Slf4j
@Data
public abstract class BaseEventHandler<TEventModelInterface extends BaseEventModel, TEventModel extends EventModel> {

    public static final String EVENT_NAME_PREFIX = "scene.event.";

    /** 事件目标 */
    private TEventModelInterface target;

    /** 事件模型类 */
    protected Class<TEventModel> eventModelClass;

    /** 当前管理事件 */
    private Integer event;

    /** 当前管理事件名称 */
    private String eventName;

    /** 事件转移表 */
    private Map<String, List<Integer>> eventTransition;

    /** 进程表 */
    private Map<String, EventProcess> eventProcessMap;

    /**
     * 获取事件锁
     */
    protected boolean acquireEventLock(int event, int eventStatus, String process, int processStatus) {
        return true;
    }

    /**
     * 释放事件锁
     */
    protected boolean releaseEventLock(int event, int eventStatus, String process, int processStatus) {
        return true;
    }

    /**
     * 保存事件状态信息
     * 1.即时同步到事件目标上
     * 2.存储事件状态（TODO）
     */
    private TEventModel saveEvent(int event, int eventStatus, String process, int processStatus, String processMessage) {
        target.setEvent(event);
        target.setEventStatus(eventStatus);
        target.setProcess(process);
        target.setProcessStatus(processStatus);
        target.setProcessMessage(processMessage);

        TEventModel eventObj = ClassUtils.createInstance(eventModelClass);
        eventObj.setTarget(target.genTarget());
        eventObj.setEvent(event);
        eventObj.setEventStatus(eventStatus);
        eventObj.setProcess(process);
        eventObj.setProcessStatus(processStatus);
        eventObj.setProcessMessage(processMessage);
        // TODO: 缓存eventObj
        return eventObj;
    }

    /**
     * 触发事件
     * @param event 事件
     * @param eventStatus 事件状态
     * @param process 事件进程
     * @param processStatus 事件进程状态
     * @param processMessage 事件进程描述
     * @return 事件触发结果
     */
    private int triggerEvent(int event, int eventStatus, String process, int processStatus, String processMessage) {
        if (acquireEventLock(event, eventStatus, process, processStatus)) {
            TEventModel eventObj = saveEvent(event, eventStatus, process, processStatus, processMessage);
            if (eventObj == null) {
                releaseEventLock(event, eventStatus, process, processStatus);
                log.error("{} save event failed: event({}), eventStatus({}), process({}), processStatus({}), processMessage({})",
                        getTargetName(), event, eventStatus, process, processStatus, processMessage);
                return EventFlag.SAVE_FAILED;
            } else {
                logProcess(event, eventStatus, process, processStatus, processMessage);
                return EventFlag.SUCCESS;
            }
        } else {
            log.error("{} enter event failed: event({}), eventStatus({}), process({}), processStatus({}), processMessage({})",
                    getTargetName(), event, eventStatus, process, processStatus, processMessage);
            return EventFlag.ENTER_FAILED;
        }
    }

    private String getProcessName(String process) {
        EventProcess eventProcess = eventProcessMap.getOrDefault(process, null);
        if (eventProcess == null) {
            log.warn("{} event({}) process({}) not in process table", getTargetName(), event, process);
            return StringUtils.EMPTY;
        }

        return eventProcess.getProcessNameText();
    }

    private String formatProcessMessage(String processMessage) {
        return StringUtils.joinWith(" ", getTargetName(), processMessage);
    }

    /**
     * 初始化
     * @param target 事件目标
     * @param event 当前管理事件
     */
    public void init(TEventModelInterface target, int event, String eventName, Map<String, List<Integer>> eventTransition) {
        this.initClass();
        this.target = target;
        this.event = event;
        this.eventName = eventName;
        this.eventTransition = eventTransition;
        this.eventProcessMap = new HashMap<>();
        checkEvent();
    }

    protected abstract void initClass();

    public BaseEventHandler(TEventModelInterface target, int event, String eventName, Map<String, List<Integer>> eventTransition) {
        init(target, event, eventName, eventTransition);
    }

    public String getTargetName() { return target.getClass().toString() + " instance"; }

    public void checkEvent() {
        List<Integer> validEvents = eventTransition.getOrDefault(EventTransition.genKey(target.getEvent(),
                target.getEventStatus()), null);
        if (validEvents == null || !validEvents.contains(event)) {
            throw new MpRuntimeException(SceneErrorCode.ENTER_EVENT_FAILED);
        }
    }

    public void addProcess(EventProcess eventProcess) {
        eventProcessMap.put(eventProcess.getProcess(), eventProcess);
    }

    public void addProcess(List<EventProcess> eventProcesses) {
        eventProcesses.forEach(this::addProcess);
    }

    /**
     * 开始事件
     */
    public int startEvent() {
        String processMessage = MessageCommon.get("scene.event.start_message", MessageCommon.get(eventName));
        return triggerEvent(event, EventStatus.START, target.getProcess(), ProcessStatus.START,
                formatProcessMessage(processMessage));
    }

    /**
     * 开始进程
     */
    public int startProcess(String process) {
        String processMessage = MessageCommon.get("scene.event.start_message", getProcessName(process));
        return triggerEvent(event, EventStatus.IN_PROGRESS, process, ProcessStatus.START,
                formatProcessMessage(processMessage));
    }

    /**
     * 进行中事件进程
     */
    public int progressProcess(String process) {
        String processMessage = MessageCommon.get("scene.event.start_message", getProcessName(process));
        return triggerEvent(event, EventStatus.IN_PROGRESS, process, ProcessStatus.IN_PROGRESS,
                formatProcessMessage(processMessage));
    }

    /**
     * 暂停事件
     */
    public int pauseProcess() {
        String processMessage = MessageCommon.get("scene.event.pause_message",
                getProcessName(target.getProcess()));
        return triggerEvent(target.getEvent(), EventStatus.PAUSE, target.getProcess(), ProcessStatus.PAUSE,
                formatProcessMessage(processMessage));
    }

    /**
     * 结束事件进程
     */
    public int overProcess() {
        String processMessage = MessageCommon.get("scene.event.over_message",
                getProcessName(target.getProcess()));
        return triggerEvent(event, EventStatus.IN_PROGRESS, target.getProcess(), ProcessStatus.OVER,
                formatProcessMessage(processMessage));
    }

    /**
     * 结束事件
     */
    public int overEvent() {
        String processMessage = MessageCommon.get("scene.event.over_message", MessageCommon.get(eventName));
        return triggerEvent(event, EventStatus.OVER, target.getProcess(), target.getProcessStatus(),
                formatProcessMessage(processMessage));
    }

    /**
     * 异常
     */
    public int error(String errorMessage) {
        String processName = getProcessName(target.getProcess());
        if (StringUtils.isEmpty(processName)) {
            processName = MessageCommon.get(eventName);
        }

        String processMessage = MessageCommon.get("scene.event.error_message", processName,
                errorMessage);
        return triggerEvent(event, EventStatus.ABNORMAL, target.getProcess(), ProcessStatus.ABNORMAL,
                formatProcessMessage(processMessage));
    }

    protected void logProcess(int event, int eventStatus, String process, int processStatus, String processMessage) {
        log.info(processMessage);
    }
}
