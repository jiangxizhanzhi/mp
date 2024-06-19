package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetCalculate;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * 扩展虚拟机信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel("计算-laas-虚拟机信息")
public class CalculateLaasVM extends DeviceCloudTargetCalculate {
    /**
     * (包含cpu数量 运存大小 网卡数量等)Standard_B1s，1vcpu，1GiB内存，2网卡
     */
    private String vmSizeType;

    /**
     * os磁盘大小
     */
    private String disk;

    /**
     * 磁盘类型(ssd/hhd)
     */
    private String vmOsDiskType;

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
    private String userName;

    /**
     * 虚拟机用户密码
     */
    private String password;

    /**
     * 虚拟机类型
     */
    private String vmType;

    /**
     * 是否支持初始化
     */
    private Boolean initSupport;

    //简化实例创建，同时允许链式调用和设置默认值
    @Builder(toBuilder = true)
    public CalculateLaasVM() {
        this.vmSizeType = "Standard_B1s";
        this.disk = "30";
        this.vmOsDiskType = "StandardSSD_LRS";
        this.loopholes = new ArrayList<>();
        this.networkInterfaces = new ArrayList<>();
        this.userName = "admin123";
        this.password = "NHiOJiZTD4K1";
        this.initSupport = true;
        List<NetworkInterface> init = new ArrayList<>();
        NetworkInterface initNet = new NetworkInterface();
        init.add(initNet);
        this.networkInterfaces = init;
    }
}
