package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetContainer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 扩展容器信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("容器-基础设施-容器注册表")
public class DockerInfrastructureContainerRegistries extends DeviceCloudTargetContainer {

    /**
     * CPU核数
     */
    private Integer cpu;

    /**
     * 内存大小
     */
    private Integer memory;

    /**
     * 网卡数量
     */
    private Integer interfaceCount;

    /**
     * 网卡设置
     */
    private List<NetworkInterface> networkInterfaces;
}
