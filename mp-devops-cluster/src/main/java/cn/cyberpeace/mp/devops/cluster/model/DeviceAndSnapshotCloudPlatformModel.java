package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.QuickCreateParam;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Snapshot;
import lombok.Data;

import java.util.List;

@Data
public class DeviceAndSnapshotCloudPlatformModel extends BaseCloudPlatformModel{

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
