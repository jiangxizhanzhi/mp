 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name InstanceModifyNetworkData.java.
 // File size 1002.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;

/**
 * @ClassName InstanceModifyNetworkData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
public class InstanceModifyNetworkData implements CloudHttpData {
    private String nodeId;
    private Boolean special;
    private List<AddNic> addNics;
    private List<DeleteNic> deleteNics;
    private String hwVifModel;
    private String type;

    @Data
    public static class AddNic {
        private String networkId;
        private String vnicName;
        private String fixedIp;
        private String cidr;
        private String gateway;
        private String macAddress;
        private VirtualNic.QosRule qosRule;
    }

    @Data
    public static class DeleteNic {
        private String interfaceId;
    }
}
