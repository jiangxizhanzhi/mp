package cn.cyberpeace.mp.devops.cluster.cloud.platform.params;

import cn.cyberpeace.mp.devops.cluster.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
