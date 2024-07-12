 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name InstanceModifyNetworkResult.java.
 // File size 679.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.Data;

import java.util.List;

/**
 * @ClassName InstanceModifyNetworkResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class InstanceModifyNetworkResult {
    private String id;
    private String name;
    private List<Nic> nics;

    @Data
    public static class Nic {
        private String id;
        private String name;
        private String macAddress;
        private String networkName;
        private String networkInterfaceId;
        private List<String> ipv4;
        private List<String> ipv6;
    }
}

