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
