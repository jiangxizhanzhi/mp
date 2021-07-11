package cn.cyberpeace.mp.scene.core.event.base;

import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldNames;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.scene.constant.SceneErrorCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ClassFieldNames({
        "eventModelClass",
})
@Slf4j
@Data
public abstract class BaseEventHandler<M extends BaseEventModel, E extends EventModel> {

    public static final String EVENT_NAME_PREFIX = "scene.event.";

    
    private M target;

    
    private Class<E> eventModelClass;

    
    private Integer event;

    
    private String eventName;

    
    private Map<String, List<Integer>> eventTransition;

    
    private Map<String, EventProcess> eventProcessMap;

    
    protected boolean acquireEventLock(int event, int eventStatus, String process, int processStatus) {
        return true;
    }

    
    protected boolean releaseEventLock(int event, int eventStatus, String process, int processStatus) {
        return true;
    }

    
    private E saveEvent(int event, int eventStatus, String process, int processStatus, String processMessage) {
        target.setEvent(event);
        target.setEventStatus(eventStatus);
        target.setProcess(process);
        target.setProcessStatus(processStatus);
        target.setProcessMessage(processMessage);

        var eventObj = ClassUtils.createInstance(eventModelClass);
        eventObj.setTarget(target.genTarget());
        eventObj.setEvent(event);
        eventObj.setEventStatus(eventStatus);
        eventObj.setProcess(process);
        eventObj.setProcessStatus(processStatus);
        eventObj.setProcessMessage(processMessage);
        
        return eventObj;
    }

    
    private int triggerEvent(int event, int eventStatus, String process, int processStatus, String processMessage) {
        if (acquireEventLock(event, eventStatus, process, processStatus)) {
            var eventObj = saveEvent(event, eventStatus, process, processStatus, processMessage);
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
        var eventProcess = eventProcessMap.getOrDefault(process, null);
        if (eventProcess == null) {
            log.warn("{} event({}) process({}) not in process table", getTargetName(), event, process);
            return StringUtils.EMPTY;
        }

        return eventProcess.getProcessNameText();
    }

    private String formatProcessMessage(String processMessage) {
        return StringUtils.joinWith(" ", getTargetName(), processMessage);
    }

    
    public void init(M target, int event, String eventName, Map<String, List<Integer>> eventTransition) {
        ClassUtils.loadClassFields(this);
        this.target = target;
        this.event = event;
        this.eventName = eventName;
        this.eventTransition = eventTransition;
        this.eventProcessMap = new HashMap<>();
        checkEvent();
    }

    public BaseEventHandler(M target, int event, String eventName, Map<String, List<Integer>> eventTransition) {
        init(target, event, eventName, eventTransition);
    }

    public String getTargetName() { return target.getClass().toString() + " instance"; }

    public void checkEvent() {
        var validEvents = eventTransition.getOrDefault(EventTransition.genKey(target.getEvent(),
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

    
    public int startEvent() {
        var processMessage = MessageCommon.get("scene.event.start_message", MessageCommon.get(eventName));
        return triggerEvent(event, EventStatus.START, target.getProcess(), ProcessStatus.START,
                formatProcessMessage(processMessage));
    }

    
    public int startProcess(String process) {
        var processMessage = MessageCommon.get("scene.event.start_message", getProcessName(process));
        return triggerEvent(event, EventStatus.IN_PROGRESS, process, ProcessStatus.START,
                formatProcessMessage(processMessage));
    }

    
    public int progressProcess(String process) {
        var processMessage = MessageCommon.get("scene.event.start_message", getProcessName(process));
        return triggerEvent(event, EventStatus.IN_PROGRESS, process, ProcessStatus.IN_PROGRESS,
                formatProcessMessage(processMessage));
    }

    
    public int pauseProcess() {
        var processMessage = MessageCommon.get("scene.event.pause_message",
                getProcessName(target.getProcess()));
        return triggerEvent(target.getEvent(), EventStatus.PAUSE, target.getProcess(), ProcessStatus.PAUSE,
                formatProcessMessage(processMessage));
    }

    
    public int overProcess() {
        var processMessage = MessageCommon.get("scene.event.over_message",
                getProcessName(target.getProcess()));
        return triggerEvent(event, EventStatus.IN_PROGRESS, target.getProcess(), ProcessStatus.OVER,
                formatProcessMessage(processMessage));
    }

    
    public int overEvent() {
        var processMessage = MessageCommon.get("scene.event.over_message", MessageCommon.get(eventName));
        return triggerEvent(event, EventStatus.OVER, target.getProcess(), target.getProcessStatus(),
                formatProcessMessage(processMessage));
    }

    
    public int error(String errorMessage) {
        var processName = getProcessName(target.getProcess());
        if (StringUtils.isEmpty(processName)) {
            processName = MessageCommon.get(eventName);
        }

        var processMessage = MessageCommon.get("scene.event.error_message", processName,
                errorMessage);
        return triggerEvent(event, EventStatus.ABNORMAL, target.getProcess(), ProcessStatus.ABNORMAL,
                formatProcessMessage(processMessage));
    }

    protected void logProcess(int event, int eventStatus, String process, int processStatus, String processMessage) {
        log.info(processMessage);
    }
}
