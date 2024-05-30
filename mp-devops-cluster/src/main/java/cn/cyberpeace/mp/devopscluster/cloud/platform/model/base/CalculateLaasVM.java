package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
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
     * (包含cpu数量 运存大小 网卡数量等)Standard_B1s，1vcpu，1GiB内存，2网卡
     */
    @DefaultStr("Standard_B1s")
    private String basicType;

    /**
     * os磁盘大小
     */
    @DefaultStr("30")
    private String osDisk;

    /**
     * 磁盘类型(ssd/hhd)
     */
    @DefaultStr("StandardSSD_LRS")
    private String hardDiskType;

    /**
     * 扩展磁盘
     */
    private String expansionDisk;

    /**
     * 关联漏洞
     */
    private List loopholes;

    /**
     * 网卡设置
     */
    private List<NetworkInterface> networkInterfaces;

    /**
     * 虚拟机用户账号
     */
    @DefaultStr("admin123")
    private String userName;

    /**
     * 虚拟机用户密码
     */
    @DefaultStr("NHiOJiZTD4K1")
    private String password;
}
