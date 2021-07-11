package cn.cyberpeace.mp.scene.core.event.base;


public interface BaseEventModel {

    String genTarget();

    Integer getEvent();

    void setEvent(Integer event);

    Integer getEventStatus();

    void setEventStatus(Integer eventStatus);

    String getProcess();

    void setProcess(String process);

    Integer getProcessStatus();

    void setProcessStatus(Integer processStatus);

    String getProcessMessage();

    void setProcessMessage(String processMessage);
}
