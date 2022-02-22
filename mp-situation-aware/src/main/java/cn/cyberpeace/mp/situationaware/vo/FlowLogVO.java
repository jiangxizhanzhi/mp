package cn.cyberpeace.mp.situationaware.vo;

import cn.cyberpeace.mp.situationaware.bo.common.AttackDestination;
import cn.cyberpeace.mp.situationaware.bo.common.AttackNetwork;
import cn.cyberpeace.mp.situationaware.bo.common.AttackSource;
import cn.cyberpeace.mp.situationaware.bo.common.AttackSuricata;
import io.swagger.annotations.ApiModelProperty;
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
public class FlowLogVO {

    /**
     * 来源
     */
    @ApiModelProperty(value = "源信息")
    private AttackSource source;

    /**
     * 目的地
     */
    @ApiModelProperty(value = "目标信息")
    private AttackDestination destination;

    /**
     * 网络
     */
    @ApiModelProperty(value = "网络信息")
    private AttackNetwork network;

    /**
     * suricata信息
     */
    @ApiModelProperty(value = "攻击信息")
    private AttackSuricata suricata;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Long timestamp;

}
