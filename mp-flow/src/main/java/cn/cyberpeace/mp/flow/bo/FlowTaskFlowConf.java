package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("流配置")
public class FlowTaskFlowConf {

    
    @ApiModelProperty("TCP配置")
    private FlowTaskTcpConf tcpConf;

    
    @ApiModelProperty("IPV4配置")
    private FlowTaskIpv4Conf ipv4Conf;

}
