 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HypervisorStatisticsCloudPlatformResult.java.
 // File size 692.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
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
