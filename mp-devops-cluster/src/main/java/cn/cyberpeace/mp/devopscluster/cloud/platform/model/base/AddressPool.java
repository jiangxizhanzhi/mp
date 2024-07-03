package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetNet;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 地址池
 */
@Data
@ApiModel("地址池")
public class AddressPool extends DeviceCloudTargetNet {

    /**
     * IPv4地址
     */
    private String ipv4Address;

    /**
     * IPv4地址池范围
     */
    private Integer ipv4AddressPool;

}
