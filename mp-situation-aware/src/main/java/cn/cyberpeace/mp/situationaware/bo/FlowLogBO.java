package cn.cyberpeace.mp.situationaware.bo;

import cn.cyberpeace.mp.situationaware.bo.common.AttackDestination;
import cn.cyberpeace.mp.situationaware.bo.common.AttackNetwork;
import cn.cyberpeace.mp.situationaware.bo.common.AttackSource;
import cn.cyberpeace.mp.situationaware.bo.common.AttackSuricata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流量日志
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowLogBO {

    /**
     * 来源
     */
    private AttackSource source;

    /**
     * 目的地
     */
    private AttackDestination destination;

    /**
     * 网络
     */
    private AttackNetwork network;

    /**
     * suricata信息
     */
    private AttackSuricata suricata;

    /**
     * 创建时间
     */
    private Long timestamp;

}
