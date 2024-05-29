package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetCalculate;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 网卡信息
 */
@Data
@ApiModel("网卡信息")
public class NetworkInterface {

    /**
     * 网卡名称
     */
    private String name;

    /**
     * 网卡id
     */
    private String id;

    /**
     * 访问类型(1:分配公网ip 2:外部网络直接访问 3:私网IP地址)
     */
    private Integer accessType;

    /**
     * ip设置
     */
    private String ip;

    /**
     * 接入协议
     */
    private String accessProtocol;
}
