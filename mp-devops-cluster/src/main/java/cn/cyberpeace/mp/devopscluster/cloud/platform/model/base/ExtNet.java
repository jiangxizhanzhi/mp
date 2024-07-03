package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetNet;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * 网络-网络服务
 */
@Data
@ApiModel("网络-网络服务")
public class ExtNet extends DeviceCloudTargetNet {

    /**
     * 地址池
     */
    List<AddressPool> addressPools;

}
