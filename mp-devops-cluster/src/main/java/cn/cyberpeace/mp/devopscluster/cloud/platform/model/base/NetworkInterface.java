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
     * ip设置
     */
    private String ip;
}
