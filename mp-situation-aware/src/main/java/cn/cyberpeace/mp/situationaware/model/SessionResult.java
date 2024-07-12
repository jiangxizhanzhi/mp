 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SessionResult.java.
 // File size 1052.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class SessionResult {

    /**
     * 分组id
     */
    @ApiModelProperty("分组id")
    private String groupId;

    @ApiModelProperty("源IP")
    private String sourceIp;

    @ApiModelProperty("目的IP")
    private String targetIp;

    @ApiModelProperty("源Mac")
    private List<String> sourceMac;

    @ApiModelProperty("目的Mac")
    private List<String> targetMac;

    @ApiModelProperty("源端口")
    private String srcPort;

    @ApiModelProperty("目的端口")
    private String dstPort;

    /**
     * 协议类型
     */
    @ApiModelProperty("协议类型")
    private List<String> protocolType;

    @ApiModelProperty("时间")
    private Long timestamp;

    @ApiModelProperty("流量大小")
    private Integer totalBytes;
}
