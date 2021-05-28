package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.base.annotation.AvailableFields;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 运维管控-集群管理-计算节点-设备管理表
 * @author wanggang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_devops_device")
public class DeviceModel extends BaseModel implements Serializable {

    /**
     * 机箱编号
     */
    private String crateId;

    /**
     * 设备组编号
     */
    private String groupId;

    /**
     * 设备编号
     */
    @AvailableFields
    private String identifier;

    /**
     * 设备名称
     */
    @AvailableFields
    private String name;

    /**
     * 设备类型
     */
    @AvailableFields
    private Integer type;

    /**
     * 操作系统
     */
    private Integer os;

    /**
     * 处理器类型
     */
    private Integer cpu;

    /**
     * 内存大小
     */
    private Integer memorySize;

    /**
     * 磁盘大小
     */
    private Integer diskSize;

    /**
     * 使用状态
     */
    private Integer useState;

    /**
     * 运行状态
     */
    private Integer runState;

    /**
     * 描述
     */
    private String description;
}
