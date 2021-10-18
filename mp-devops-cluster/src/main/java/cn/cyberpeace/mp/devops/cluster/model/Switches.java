package cn.cyberpeace.mp.devops.cluster.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Switches {

    @ApiModelProperty("交换机名称")
    private String name;

    @ApiModelProperty("管理IP")
    private String managementIp;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("接口数量")
    private Integer interfaceNums;

    @ApiModelProperty("启用中的接口明细")
    private List<String> interfaceUpDetails;

    @ApiModelProperty("启用中接口数量")
    private Integer interfaceUpNums;

    @ApiModelProperty("vlan数量")
    private Integer vlanNums;

    @ApiModelProperty("接口明细")
    private List<InterfaceDetail> interfaceDetails;
}
