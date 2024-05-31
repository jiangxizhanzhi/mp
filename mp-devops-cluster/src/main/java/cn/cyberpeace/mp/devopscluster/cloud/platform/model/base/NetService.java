package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetNet;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 网络服务
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("网络-网络服务")
public class NetService extends DeviceCloudTargetNet {

}
