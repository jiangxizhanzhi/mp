package cn.cyberpeace.mp.situationaware.model.enums;

/**
 * 聚合类型
 * @author wanggang
 */
public enum AggregationTypeEnum {

    /**
     * 源IP
     */
    SOURCE_IP,

    /**
     * 目标IP
     */
    DESTINATION_IP,

    /**
     * 攻击分类
     */
    SURICATA_CATEGORY,

    /**
     * 攻击严重性
     */
    SURICATA_SEVERITY
}
