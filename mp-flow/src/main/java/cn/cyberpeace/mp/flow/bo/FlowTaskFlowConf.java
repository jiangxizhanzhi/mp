package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 流配置
 * @Author: guquan
 * @Date：2023/5/9 13:55
 */
@Data
@ApiModel("流配置")
public class FlowTaskFlowConf {

    /**
     * TCP配置
     */
    @ApiModelProperty("TCP配置")
    private FlowTaskTcpConf tcpConf;

    /**
     * IPV4配置
     */
    @ApiModelProperty("IPV4配置")
    private FlowTaskIpv4Conf ipv4Conf;

}
