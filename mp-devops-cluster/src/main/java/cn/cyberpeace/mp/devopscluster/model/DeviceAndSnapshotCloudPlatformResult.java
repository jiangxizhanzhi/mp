package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.QuickCreateParam;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Snapshot;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import lombok.Data;

import java.util.List;

@Data
public class DeviceAndSnapshotCloudPlatformResult extends BaseCloudPlatformResult {

    
    private String deviceUuid;

    
    private String deviceName;

    
    private String imageName;

    
    private List<Snapshot> snapshots;

    private List<QuickCreateParam> quickCreateParams;

}
