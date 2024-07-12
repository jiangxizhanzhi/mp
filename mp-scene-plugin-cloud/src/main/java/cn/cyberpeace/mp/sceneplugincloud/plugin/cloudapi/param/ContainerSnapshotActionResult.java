 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ContainerSnapshotActionResult.java.
 // File size 368.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.Data;

/**
 * @ClassName ContainerSnapshotActionResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class ContainerSnapshotActionResult {
    private String containerId;
    private Boolean success;
    private String message;
}