 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DeviceSnapshotMergeCloudPlatform.java.
 // File size 791.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName DeviceSnapshotMergeCloudPlatform
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSnapshotMergeCloudPlatform extends BaseModel {

    /**
     * 镜像ID
     */
    private String deviceUuid;

    /**
     * 镜像类型
     */
    private String imageType;

    /**
     * 最新一条的快照名称
     */
    private String snapshotName;

    /**
     * 新的快照名称
     */
    private String newName;
}
