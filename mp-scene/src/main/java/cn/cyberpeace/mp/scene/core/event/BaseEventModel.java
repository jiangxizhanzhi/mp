package cn.cyberpeace.mp.scene.core.event;

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
