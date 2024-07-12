 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ContainerAddPortData.java.
 // File size 645.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ContainerAddPortData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContainerAddPortData implements CloudHttpData {
    private String networkId;
    private String fixedIp;
    private String containerId;

    private String macAddress;
    private String portId;
}
