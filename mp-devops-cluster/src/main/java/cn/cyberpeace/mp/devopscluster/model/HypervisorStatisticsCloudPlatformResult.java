package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.model.common.HypervisorStatistics;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class HypervisorStatisticsCloudPlatformResult extends BaseCloudPlatformResult {

    
    private HypervisorStatistics hypervisorStatistics;


    
}
