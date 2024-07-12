 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowTaskFlowConf.java.
 // File size 565.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FlowTaskFlowConf
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
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
