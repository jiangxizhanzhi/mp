package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "防火墙分页返回信息")
public class FirewallPageVO extends SceneInfoVO {

    
    @ApiModelProperty("名称")
    private String name;

    
    @ApiModelProperty("入站策略")
    private String ingressFirewallPolicy;

    
    @ApiModelProperty("出站策略")
    private String egressFirewallPolicy;

    
    @ApiModelProperty("描述")
    private String description;

    
    @ApiModelProperty("状态")
    private String status;

}
