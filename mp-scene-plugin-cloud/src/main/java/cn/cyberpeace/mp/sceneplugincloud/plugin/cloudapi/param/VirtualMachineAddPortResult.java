 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualMachineAddPortResult.java.
 // File size 664.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName VirtualMachineAddPortResult
 * @Description
 * @Author maojun
 * @Date 2022/6/8
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualMachineAddPortResult implements CloudHttpData {
    private String fixedIp;
    private String portId;
    private String macAddress;
    private Boolean portSecurityEnabled;

    private String id;
    private String name;
    private String hwVifModel;
    private String networkId;
}
