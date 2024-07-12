 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowNetworkInfo.java.
 // File size 741.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FlowNetworkInfo
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
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
