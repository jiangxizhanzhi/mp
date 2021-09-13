package cn.cyberpeace.mp.devops.cluster.cloud.platform.model;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.ContainerModel;
import cn.cyberpeace.mp.devops.cluster.model.common.RouterModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouterPageResult extends BaseCloudPlatformModel {

    private CloudPlatformPageResult<RouterModel> data;
}
