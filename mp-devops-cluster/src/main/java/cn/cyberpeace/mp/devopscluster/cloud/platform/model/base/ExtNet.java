package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetNet;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 网络-网络服务
 */
@Data
@ApiModel("网络-网络服务")
public class ExtNet extends DeviceCloudTargetNet {

    /**
     * 接入方式
     */
    @ApiModelProperty("接入方式")
    private List<AccessProtocol> accessProtocols;

    /**
     * IPv4地址
     */
    private String ipv4Address;

    /**
     * IPv4地址池
     */
    private String ipv4AddressPool;

}
