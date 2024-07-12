 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualDynamicCreateData.java.
 // File size 1079.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;

/**
 * @ClassName VirtualDynamicCreateData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
public class VirtualDynamicCreateData implements CloudHttpData {
    private String name;
    private String image;
    private String host;
    private Integer portSum;
    private List<Network> networks;

    // 返回值
    private String platform;
    private String deviceUuid;
    private String id;
    private String instanceId;
    private Integer telnetPort;
    private String telnetHost;
    private String createdAt;
    private String status ;

    @Data
    public static class Network {
        private String networkId;
        private String slotPort;
        private String macAddress;
        private String fixedIp;
        private String portId;
        private String tapId;
    }
}
