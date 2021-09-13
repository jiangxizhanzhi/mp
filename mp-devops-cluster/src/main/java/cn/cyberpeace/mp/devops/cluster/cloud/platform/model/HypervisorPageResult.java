package cn.cyberpeace.mp.devops.cluster.cloud.platform.model;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.Hypervisor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HypervisorPageResult extends BaseCloudPlatformModel {

    private CloudPlatformPageResult<Hypervisor> data;
}
