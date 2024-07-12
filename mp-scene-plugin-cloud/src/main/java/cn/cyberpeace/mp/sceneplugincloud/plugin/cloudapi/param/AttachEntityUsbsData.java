 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AttachEntityUsbsData.java.
 // File size 572.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;

/**
 * @ClassName AttachEntityUsbsData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
public class AttachEntityUsbsData implements CloudHttpData {

    /**
     * 虚拟机uuid
     */
    private String instanceUuid;

    /**
     * 实体usb靶标uuid
     */
    private List<String> entityUsbUuids;
}
