package cn.cyberpeace.mp.situationaware.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聚合攻击目标IP
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttackDestinationIpAggregationBO {

    /**
     * 攻击目标IP
     */
    private String attackDestinationIp;


    /**
     * 攻击次数
     */
    private Long num;
}
