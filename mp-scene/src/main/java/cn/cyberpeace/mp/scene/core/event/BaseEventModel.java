package cn.cyberpeace.mp.scene.core.event;

/**
 * 通用事件模型
 *  event: 事件（目标做某件事）
 *  eventStatus: 事件状态（当前这件事的状态）
 *  process: 事件进程（这件事进行到某个阶段）
 *  processStatus: 事件进程状态（这件事在这个阶段的状态）
 *  processMessage: 进程信息（这件事在这个阶段状态的具体说明信息）
 */
public interface BaseEventModel {
    Integer getEvent();

    void setEvent(Integer event);

    Integer getEventStatus();

    void setEventStatus(Integer eventStatus);

    Integer getProcess();

    void setProcess(Integer process);

    Integer getProcessStatus();

    void setProcessStatus(Integer processStatus);

    Integer getProcessMessage();

    void setProcessMessage(Integer processMessage);
}
