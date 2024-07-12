 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualMachineRestartData.java.
 // File size 720.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName VirtualMachineRestartData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Setter
@Getter
@AllArgsConstructor
public class VirtualMachineRestartData implements CloudHttpData {
    private Boolean waitForStatus = true;
    private Map reboot = new HashMap();

    public VirtualMachineRestartData() {
//        this.reboot.put("reboot_type", "HARD");
    }
}
