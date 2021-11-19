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
     * 攻击方式
     */
    private String attackWay;

    /**
     *
     */
    private String description;
}
