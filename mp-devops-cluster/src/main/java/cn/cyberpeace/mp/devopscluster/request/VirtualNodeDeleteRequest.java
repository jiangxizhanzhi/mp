 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualNodeDeleteRequest.java.
 // File size 405.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import cn.cyberpeace.mp.devopscluster.cloud.platform.params.InstanceDeleteData;
import lombok.Data;

import java.util.List;

/**
 * @ClassName VirtualNodeDeleteRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class VirtualNodeDeleteRequest {
    private List<InstanceDeleteData> instances;
}