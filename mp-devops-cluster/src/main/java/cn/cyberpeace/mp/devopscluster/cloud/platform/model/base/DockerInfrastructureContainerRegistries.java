package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetContainer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 扩展容器信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("容器-基础设施-容器注册表")
public class DockerInfrastructureContainerRegistries extends DeviceCloudTargetContainer {


}
