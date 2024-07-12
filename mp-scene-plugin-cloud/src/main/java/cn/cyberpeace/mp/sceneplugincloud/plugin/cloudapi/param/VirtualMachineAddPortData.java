 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualMachineAddPortData.java.
 // File size 476.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

/**
 * @ClassName VirtualMachineAddPortData
 * @Description
 * @Author maojun
 * @Date 2022/6/8
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
public class VirtualMachineAddPortData implements CloudHttpData {
    private String networkId;
    private String fixedIp;
    private String portId;
}
