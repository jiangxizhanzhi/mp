package cn.cyberpeace.mp.situationaware.model.enums;

/**
  *@Desciption 数据采集
  *@Author zhangjin
  *@Date 12/2/21 9:49 上午
**/
public enum DataCollectionType {
    LOGINSUCCESS("5715", "登录成功"),
    LOGOUT("5502", "登录失败");

    private String ruleId;

    private String eventDes;

    DataCollectionType(String ruleId, String eventDes) {
        this.ruleId = ruleId;
        this.eventDes = eventDes;
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getEventDes() {
        return eventDes;
    }
}
