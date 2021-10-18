package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.model.common.HypervisorStatistics;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class HypervisorStatisticsCloudPlatformModel extends BaseCloudPlatformModel {

    /**
     * 资源统计
     */
    private HypervisorStatistics hypervisorStatistics;


    
}
