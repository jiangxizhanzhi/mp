package cn.cyberpeace.mp.devops.cluster.cloud.platform;

import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudPlatformResult<T> extends BaseCloudPlatformModel {

    private T data;
}
