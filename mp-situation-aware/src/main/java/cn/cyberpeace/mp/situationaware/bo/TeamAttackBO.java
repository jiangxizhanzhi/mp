 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TeamAttackBO.java.
 // File size 414.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.bo;

import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamAttackBO extends BaseBO{


    /**
     * 战队名称
     */
    private String teamName;

    /**
     * 攻击手法列表
     */
    private List<AttackWayAggregationBO> attackWayList;

}
