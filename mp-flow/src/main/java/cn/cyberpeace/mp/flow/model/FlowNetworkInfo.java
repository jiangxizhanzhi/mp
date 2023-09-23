package cn.cyberpeace.mp.flow.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 流量网络信息
 * @Author qiaokang
 * @Date：2022/4/28 13:48
 */
@Data
@ApiModel(value = "流量网络信息")
public class FlowNetworkInfo {

    /**
     * ip
     */
    @ApiModelProperty("ip")
    private String ip;

    /**
     * 网络id
     */
    @ApiModelProperty("网络id")
    private String networkId;

    /**
     * mac
     */
    @ApiModelProperty("mac")
    private String mac;

    /**
     * 节点名称
     */
    @ApiModelProperty("节点名称")
    private String nodeName;

}
