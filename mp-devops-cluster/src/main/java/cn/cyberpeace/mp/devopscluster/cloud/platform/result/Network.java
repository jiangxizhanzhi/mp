 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Network.java.
 // File size 798.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.result;

import lombok.Data;

import java.util.List;

/**
 * @ClassName Network
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class Network {
    private String id;
    private String name;
    private String description;
    private String networkType;
    private String status;
    private String mtu;
    private String cidr;
    private String comment;
    private Subnet subnet;
    private String createAt;
    private String runningTime;

    private String sceneName;

    @Data
    public static class Subnet {
        private String name;
        private String cidr;
        private String gatewayIp;
        private List<String> dnsNameservers;
    }
}