 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AttackWayAggregationBO.java.
 // File size 352.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.bo;

import lombok.*;

/**
 * 聚合攻击方式
 * @author wanggang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttackWayAggregationBO extends BaseBO {

    /**
     * 聚合类型
     */
    private String aggregationType;
}
