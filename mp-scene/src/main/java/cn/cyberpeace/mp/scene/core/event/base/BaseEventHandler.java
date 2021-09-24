package cn.cyberpeace.mp.scene.core.event.base;

import cn.cyberpeace.mp.base.annotation.ClassFieldNames;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.MessageUtils;
import cn.cyberpeace.mp.scene.constant.SceneErrorCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 事件处理
 * @param <M> 事件目标模型类
 * @param <E> 事件模型类
 *           1.不做特殊处理
 *           2.当前最新事件状态存储到缓存（TODO）
 *           3.事件每一个状态变化都持久化（暂不实现）
 */
@ClassFieldNames({
        "eventModelClass",
})
@Slf4j
@Data
public abstract class BaseEventHandler<M extends BaseEventModel, E extends EventModel> {

    /** 事件目标 */
    private M target;

    /** 事件模型类 */
    private Class<E> eventModelClass;

    /** 当前管理事件 */
    private Integer event;

    /** 当前管理事件名称 */
    private String eventName;

    /** 事件转移表 */
    private Map<String, List<Integer>> eventTransition;

    /**
     * 获取事件锁
     */
    private boolean acquireEventLock(int event, String process, int processStatus) {
        return true;
    }

    /**
     * 释放事件锁
     */
    private boolean releaseEventLock(int event, String process, int processStatus) {
        return true;
    }

    /**
     * 保存事件状态信息
     * 1.即时同步到事件目标上
     * 2.存储事件状态（TODO）
     */
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
        if (acquireEventLock(event, process, processStatus)) {
            var eventObj = saveEvent(event, eventStatus, process, processStatus, processMessage);
            if (eventObj == null) {
                releaseEventLock(event, process, processStatus);
                log.error("{} save event failed: event({}), eventStatus({}), process({}), processStatus({}), processMessage({})",
                        getTargetName(), event, eventStatus, process, processStatus, processMessage);
                return EventFlag.SAVE_FAILED;
            } else {
                log.info(processMessage);
                return EventFlag.SUCCESS;
            }
        } else {
            log.error("{} enter event failed: event({}), eventStatus({}), process({}), processStatus({}), processMessage({})",
                    getTargetName(), event, eventStatus, process, processStatus, processMessage);
            return EventFlag.ENTER_FAILED;
        }
    }

    /**
     * 初始化
     * @param target 事件目标
     * @param event 当前管理事件
     */
    public void init(M target, int event, String eventName, Map<String, List<Integer>> eventTransition) {
        ClassUtils.loadClassFields(this);
        this.target = target;
        this.event = event;
        this.eventName = eventName;
        this.eventTransition = eventTransition;
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

    /**
     * 开始事件
     */
    public int startEvent() {
        var processMessage = MessageUtils.get("scene.event.start_message", getTargetName(),
                MessageUtils.get(eventName));
        return triggerEvent(event, EventStatus.START, target.getProcess(), target.getProcessStatus(), processMessage);
    }

    /**
     * 开始进程
     */
    public int startProcess(String process, String processMessage) {
        return triggerEvent(event, EventStatus.IN_PROGRESS, process, ProcessStatus.START, processMessage);
    }

    /**
     * 进行中事件进程
     */
    public int progressProcess(String process, String processMessage) {
        return triggerEvent(event, EventStatus.IN_PROGRESS, process, ProcessStatus.IN_PROGRESS, processMessage);
    }

    /**
     * 暂停事件
     */
    public int pauseProcess(String processMessage) {
        processMessage = StringUtils.isEmpty(processMessage) ?
                MessageUtils.get("scene.event.pause_message", getTargetName(), MessageUtils.get(eventName)) :
                processMessage;
        return triggerEvent(target.getEvent(), EventStatus.PAUSE, target.getProcess(), ProcessStatus.PAUSE,
                processMessage);
    }

    /**
     * 结束事件进程
     */
    public int overProcess(String processMessage) {
        return triggerEvent(event, EventStatus.IN_PROGRESS, target.getProcess(), ProcessStatus.OVER, processMessage);
    }

    /**
     * 结束事件
     */
    public int overEvent() {
        var processMessage = MessageUtils.get("scene.event.over_message", getTargetName(),
                MessageUtils.get(eventName));
        return triggerEvent(event, EventStatus.OVER, target.getProcess(), target.getProcessStatus(), processMessage);
    }

    /**
     * 异常事件
     */
    public int error(String processMessage) {
        processMessage = MessageUtils.get("scene.event.error_message", getTargetName(),
                MessageUtils.get(eventName), processMessage);
        return triggerEvent(event, EventStatus.ABNORMAL, target.getProcess(), ProcessStatus.ABNORMAL, processMessage);
    }

}
