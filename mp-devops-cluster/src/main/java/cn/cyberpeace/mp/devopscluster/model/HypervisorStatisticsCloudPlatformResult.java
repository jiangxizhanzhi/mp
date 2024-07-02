package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.cscene.response.HypervisorStatistics;
import lombok.*;

/**
 * @ClassName HypervisorStatisticsCloudPlatformResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPlatformResult
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class HypervisorStatisticsCloudPlatformResult extends BaseCloudPlatformResult {

    /**
     * 资源统计
     */
    private HypervisorStatistics hypervisorStatistics;


    
}
