package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetNet;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 地址池
 */
@Data
@ApiModel("子网地址池")
public class AddressPool extends DeviceCloudTargetNet {

    /**
     * IPv4起始地址
     */
    private String ipv4StartAddress;

    /**
     * IPv4地址范围
     */
    private Integer ipv4AddressPool;

}
