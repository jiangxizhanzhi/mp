package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
