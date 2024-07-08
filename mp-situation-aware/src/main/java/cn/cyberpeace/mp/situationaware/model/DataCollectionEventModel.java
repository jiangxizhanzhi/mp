package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.situationaware.model.enums.DataCollectionType;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 数据采集事件模型类。
 * 用于封装数据采集相关的事件信息，包括事件描述、采集类型、时间等。
 */
public class DataCollectionEventModel {

    // 规则ID，用于标识数据采集规则。
    private String ruleId;

    // Agent名称，标识进行数据采集的代理名称。
    private String agentName;

    // 事件描述，对采集事件进行详细说明。
    private String eventDes;

    // 数据采集类型，用于区分不同的数据采集方式。
    private DataCollectionType dataCollectionType;

    // 事件发生时间，记录数据采集事件的具体时间。
    private Date time;

    /**
     * 获取规则ID。
     *
     * @return 规则ID字符串。
     */
    public String getRuleId() {
        return ruleId;
    }

    /**
     * 设置规则ID。
     *
     * @param ruleId 要设置的规则ID字符串。
     */
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    /**
     * 获取Agent名称。
     *
     * @return Agent名称字符串。
     */
    public String getAgentName() {
        return agentName;
    }

    /**
     * 设置Agent名称。
     *
     * @param agentName 要设置的Agent名称字符串。
     */
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    /**
     * 获取事件描述。
     *
     * @return 事件描述字符串。
     */
    public String getEventDes() {
        return eventDes;
    }

    /**
     * 设置事件描述。
     *
     * @param eventDes 要设置的事件描述字符串。
     */
    public void setEventDes(String eventDes) {
        this.eventDes = eventDes;
    }

    /**
     * 获取数据采集类型。
     *
     * @return 数据采集类型对象。
     */
    public DataCollectionType getDataCollectionType() {
        return dataCollectionType;
    }

    /**
     * 设置数据采集类型。
     *
     * @param dataCollectionType 要设置的数据采集类型对象。
     */
    public void setDataCollectionType(DataCollectionType dataCollectionType) {
        this.dataCollectionType = dataCollectionType;
    }

    /**
     * 获取事件发生时间。
     *
     * @return 事件发生时间对象。
     */
    public Date getTime() {
        return time;
    }

    /**
     * 设置事件发生时间。
     *
     * @param time 要设置的事件发生时间对象。
     */
    public void setTime(Date time) {
        this.time = time;
    }
}
