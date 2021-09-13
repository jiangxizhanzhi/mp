package cn.cyberpeace.mp.devops.cluster.cloud.platform.model;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.InstanceModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstancePageResult extends BaseCloudPlatformModel {

    private CloudPlatformPageResult<InstanceModel> data;
}
