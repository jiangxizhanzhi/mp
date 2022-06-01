package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Snapshot extends BaseCloudPlatformResult {

    /**
     * UUID
     */
    private String uuid;

    /**
     * 真实快照名称
     */
    private String name;

    /**
     * 用户快照名称
     */
    private String userSnapshotName;

    /**
     * 设备ID
     */
    private String deviceUuid;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private String createdAt;
}
