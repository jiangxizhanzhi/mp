package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 网络端口详细信息
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("接口明细")
public class InterfaceDetail implements Serializable {

    @ApiModelProperty("端口号")
    private String interfaceName;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("端口类型，全部为以太类型")
    private String type;

    @ApiModelProperty("端口速率M")
    private String speed;

    private String loopback;

    @ApiModelProperty(" # 双工模式，FULL为全双工，HALF为半双工")
    private String duplex;

    @ApiModelProperty("# 双工协商，ENABLE为自协商，DISABLE为关闭自协商")
    private String negotiation;

    private String linkType;

    private String permitVlan;

    private String hwAddr;

    private String inputTrafficPackets;

    private String inputTrafficBytes;

    private String outputTrafficPackets;

    private String outputTrafficBytes;
}
