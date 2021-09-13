package cn.cyberpeace.mp.devops.cluster.cloud.platform.model;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.CloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.model.common.HypervisorStatistics;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class HypervisorStatisticsResult extends CloudPlatformResult<HypervisorStatistics> {
}
