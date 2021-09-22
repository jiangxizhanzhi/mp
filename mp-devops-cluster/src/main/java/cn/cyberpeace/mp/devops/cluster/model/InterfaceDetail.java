package cn.cyberpeace.mp.devops.cluster.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("接口明细")
public class InterfaceDetail {

    @ApiModelProperty("接口名称")
    private String interfaceName;

    @ApiModelProperty("状态")
    private String status;

    private String linkType;

    private String permitVlan;

    private String hwAddr;

    private String inputTrafficPackets;

    private String inputTrafficBytes;

    private String outputTrafficPackets;

    private String outputTrafficBytes;
}
