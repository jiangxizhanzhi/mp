package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSnapshotMergeCloudPlatform extends BaseModel {

    
    private String deviceUuid;

    
    private String imageType;

    
    private String snapshotName;

    
    private String newName;
}
