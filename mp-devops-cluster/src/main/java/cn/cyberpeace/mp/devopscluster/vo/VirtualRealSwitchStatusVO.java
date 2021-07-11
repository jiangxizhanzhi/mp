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
@ApiModel(value = "虚实结合交换机状态")
public class VirtualRealSwitchStatusVO extends BaseVO {

    
    @ApiModelProperty("名称")
    private String name;

    
    @ApiModelProperty("管理IP")
    private String managementIp;

    
    @ApiModelProperty("节点管理")
    private String state;

    
    @ApiModelProperty("端口总数")
    private Integer ports;

    
    @ApiModelProperty("已使用端口数")
    private Integer usedPorts;
}
