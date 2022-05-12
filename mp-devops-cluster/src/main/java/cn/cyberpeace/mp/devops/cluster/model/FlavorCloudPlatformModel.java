package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Flavor;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageData;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class FlavorCloudPlatformModel extends BaseCloudPlatformModel {

    private CloudPlatformPageData<Flavor> data;
}
