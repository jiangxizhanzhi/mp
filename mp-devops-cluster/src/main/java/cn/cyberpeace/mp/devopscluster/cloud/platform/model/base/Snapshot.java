package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Snapshot extends BaseCloudPlatformResult {

    
    private String uuid;

    
    private String name;

    
    private String userSnapshotName;

    
    private String deviceUuid;

    
    private String description;

    
    private String createdAt;

    private String imageName;

    private ExtInfo extInfo;

    @Setter
    @Getter
    public static class ExtInfo{
        private String sceneId ;
    }
}
