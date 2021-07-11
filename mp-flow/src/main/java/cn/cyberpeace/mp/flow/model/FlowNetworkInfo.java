package cn.cyberpeace.mp.flow.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "流量网络信息")
public class FlowNetworkInfo {

    
    @ApiModelProperty("ip")
    private String ip;

    
    @ApiModelProperty("网络id")
    private String networkId;

    
    @ApiModelProperty("mac")
    private String mac;

    
    @ApiModelProperty("节点名称")
    private String nodeName;

}
