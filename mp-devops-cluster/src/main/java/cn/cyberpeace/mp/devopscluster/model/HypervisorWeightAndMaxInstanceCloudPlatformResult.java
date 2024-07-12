 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HypervisorWeightAndMaxInstanceCloudPlatformResult.java.
 // File size 785.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @ClassName HypervisorWeightAndMaxInstanceCloudPlatformResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPlatformResult
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HypervisorWeightAndMaxInstanceCloudPlatformResult extends BaseCloudPlatformResult {

    /**
     * 权重
     */
   private Integer weight;

    /**
     * 虚拟机数量限制
     */
   private Integer maxInstances;
}
