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
