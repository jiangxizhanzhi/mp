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

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowLogVO {

    
    @ApiModelProperty(value = "源信息")
    private AttackSource source;

    
    @ApiModelProperty(value = "目标信息")
    private AttackDestination destination;

    
    @ApiModelProperty(value = "网络信息")
    private AttackNetwork network;

    
    @ApiModelProperty(value = "攻击信息")
    private AttackSuricata suricata;

    
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime timestamp;

}
