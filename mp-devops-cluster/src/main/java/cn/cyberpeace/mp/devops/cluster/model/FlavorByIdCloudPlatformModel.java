package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Flavor;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FlavorByIdCloudPlatformModel extends CloudPlatformResult<Flavor> {

}
