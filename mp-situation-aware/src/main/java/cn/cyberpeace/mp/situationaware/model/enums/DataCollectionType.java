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

    private String eventType;

    DataCollectionType(String ruleId, String eventType) {
        this.ruleId = ruleId;
        this.eventType = eventType;
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getEventType() {
        return eventType;
    }
}
