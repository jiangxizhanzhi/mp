 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DeviceAndSnapshotCloudPlatformResult.java.
 // File size 963.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.QuickCreateParam;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.result.Snapshot;
import lombok.Data;

import java.util.List;

/**
 * @ClassName DeviceAndSnapshotCloudPlatformResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPlatformResult
 */
@Data
public class DeviceAndSnapshotCloudPlatformResult extends BaseCloudPlatformResult {

    /**
     * 设备ID
     */
    private String deviceUuid;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 镜像名称
     */
    private String imageName;

    /**
     * 快照列表
     */
    private List<Snapshot> snapshots;

    private List<QuickCreateParam> quickCreateParams;

}
