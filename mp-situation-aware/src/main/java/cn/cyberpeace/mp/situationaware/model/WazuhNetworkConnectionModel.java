 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WazuhNetworkConnectionModel.java.
 // File size 938.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WazuhNetworkConnectionModel {

    @JSONField(name = "pid")
    private String pid;

    @JSONField(name = "status")
    private String status;

    @JSONField(name = "proto")
    private String proto;

    @JSONField(name = "laddr_ip")
    private String laddrIp;

    @JSONField(name = "laddr_port")
    private String laddrPort;

    @JSONField(name = "raddr_ip")
    private String raddrIp;

    @JSONField(name = "raddr_port")
    private String raddrPort;

    private String description;

    private LocalDateTime time;

    @JSONField(name = "level")
    private int level;
}
