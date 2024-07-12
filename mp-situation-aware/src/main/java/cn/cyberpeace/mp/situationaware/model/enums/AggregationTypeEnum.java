 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AggregationTypeEnum.java.
 // File size 382.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
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
