 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualMachineResizeData.java.
 // File size 648.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName VirtualMachineResizeData
 * @Description
 * @Author zhanglin
 * @Date 2022/10/12
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VirtualMachineResizeData implements CloudHttpData {
    private String flavor;
    private String instanceId;
}
