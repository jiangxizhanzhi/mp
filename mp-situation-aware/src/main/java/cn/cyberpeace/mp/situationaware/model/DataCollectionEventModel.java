package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.situationaware.model.enums.DataCollectionType;
import java.time.LocalDateTime;
import java.util.Date;


public class DataCollectionEventModel {

    private String ruleId;

    private String agentName;

    private String eventDes;

    private DataCollectionType dataCollectionType;

    private Date time;

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getEventDes() {
        return eventDes;
    }

    public void setEventDes(String eventDes) {
        this.eventDes = eventDes;
    }

    public DataCollectionType getDataCollectionType() {
        return dataCollectionType;
    }

    public void setDataCollectionType(DataCollectionType dataCollectionType) {
        this.dataCollectionType = dataCollectionType;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
