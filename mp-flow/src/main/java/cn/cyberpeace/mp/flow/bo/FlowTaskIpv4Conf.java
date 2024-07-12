 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowTaskIpv4Conf.java.
 // File size 704.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FlowTaskIpv4Conf
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@ApiModel("ipv4配置")
public class FlowTaskIpv4Conf {

    /**
     * TTL
     */
    @ApiModelProperty("TTL")
    private Integer ttl;

    /**
     * DSCP
     */
    @ApiModelProperty("DSCP")
    private String dscp;

    /**
     * 源IP
     */
    @ApiModelProperty("源IP")
    private String sip;

    /**
     * 目的IP
     */
    @ApiModelProperty("目的IP")
    private String dip;

}
