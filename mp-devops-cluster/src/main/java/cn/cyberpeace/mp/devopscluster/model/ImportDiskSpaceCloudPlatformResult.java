package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import lombok.*;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ImportDiskSpaceCloudPlatformResult extends BaseCloudPlatformResult implements Serializable {

    
    private DiskSpace data;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static final class DiskSpace implements Serializable {

        
        private Long vmSpace;

    }
}
