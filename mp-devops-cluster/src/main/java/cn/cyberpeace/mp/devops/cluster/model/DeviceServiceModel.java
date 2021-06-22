package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 运维管控-集群管理-计算节点-设备服务表
 * @author wanggang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_devops_device_service")
public class DeviceServiceModel extends BaseModel implements Serializable {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 虚拟机数量
     */
    private Integer vmNum;

    /**
     * 容器数量
     */
    private Integer containerNum;

    /**
     * 实装装备数量
     */
    private Integer realDeviceNum;

    /**
     * NPC数量
     */
    private Integer npcNum;

    /**
     * 交换机数量
     */
    private Integer switchNum;

    /**
     * 路由器数量
     */
    private Integer routerNum;

    /**
     * 防火墙数量
     */
    private Integer firewallNum;

    /**
     * 网桥数量
     */
    private Integer bridgeNum;
}
