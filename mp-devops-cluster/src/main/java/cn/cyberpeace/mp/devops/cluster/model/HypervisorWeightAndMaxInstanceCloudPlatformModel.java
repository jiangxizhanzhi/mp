package cn.cyberpeace.mp.devops.cluster.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HypervisorWeightAndMaxInstanceCloudPlatformModel extends BaseCloudPlatformModel {

    /**
     * 权重
     */
   private Integer weight;

    /**
     * 虚拟机数量限制
     */
   private Integer maxInstances;
}
