 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogicalSwitchCreateData.java.
 // File size 676.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;

/**
 * @ClassName LogicalSwitchCreateData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
public class LogicalSwitchCreateData implements CloudHttpData {
    private String logicalSwitchName;
    private List<PhysicalPort> physicalPorts;
    private String physicalSwitchName;

    @Data
    public static class PhysicalPort {
        private String port;
        // 2-4094
        private Integer vlan;
    }
}