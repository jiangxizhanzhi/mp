 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ContainerFileGetData.java.
 // File size 629.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ContainerFileGetData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContainerFileGetData implements CloudHttpData {
    private String downloadType = "ever";
    private String zunContainerId;
    private String hostname;
    private String filePath;
}
