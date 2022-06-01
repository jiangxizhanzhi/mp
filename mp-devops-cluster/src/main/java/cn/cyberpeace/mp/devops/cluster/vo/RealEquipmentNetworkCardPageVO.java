package cn.cyberpeace.mp.devops.cluster.vo;

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

    /**
     * 名称
     */
    @ApiModelProperty("接口名称")
    private String name;

    /**
     * 入流量
     */
    @ApiModelProperty("入流量")
    private Long inflow;

    /**
     * 出流量
     */
    @ApiModelProperty("出流量")
    private Long outflow;

}
