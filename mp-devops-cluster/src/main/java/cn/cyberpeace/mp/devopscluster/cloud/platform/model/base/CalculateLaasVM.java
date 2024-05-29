package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetCalculate;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 扩展虚拟机信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("计算-laas-虚拟机信息")
public class CalculateLaasVM extends DeviceCloudTargetCalculate {
    /**
     * CPU核数
     */
    private Integer cpu;

    /**
     * 内存大小
     */
    private Integer ram;

    /**
     * 磁盘大小
     */
    private Integer disk;

    /**
     * 网卡数量
     */
    private Integer netInterface;

    /**
     * 是否连接外网
     */
    private Boolean isConnectInt;

    /**
     * 网卡设置
     */
    private List<NetworkInterface> networkInterfaces;

    /**
     * 接入协议
     */
    private String accessProtocol;

    /**
     * 虚拟机用户账号
     */
    private String userName;

    /**
     * 虚拟机用户账号
     */
    private String password;

    /**
     * 关联漏洞
     */
    private List loopholes;
}
