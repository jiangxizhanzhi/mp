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
@ApiModel(description = "系统运维-实装管理接口资源返回信息")
public class RealEquipmentNetworkCardPageVO extends BaseVO {

    
    @ApiModelProperty("接口名称")
    private String name;

    
    @ApiModelProperty("入流量")
    private Long inflow;

    
    @ApiModelProperty("出流量")
    private Long outflow;

}
