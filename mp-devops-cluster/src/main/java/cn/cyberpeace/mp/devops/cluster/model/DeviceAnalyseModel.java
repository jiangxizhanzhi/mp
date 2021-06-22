package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 运维管控-集群管理-计算节点-设备参数分析表
 * @author wanggang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_devops_device_analyse")
public class DeviceAnalyseModel extends BaseModel implements Serializable {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 处理器使用率
     */
    private BigDecimal cpuUseRate;

    /**
     * 磁盘已使用大小
     */
    private Integer diskUseSize;

    /**
     * 地址池使用率
     */
    private BigDecimal addressPoolUseRate;

    /**
     * 磁盘IO
     */
    private BigDecimal diskIo;

    /**
     * 网络IO
     */
    private Integer networkIo;

    /**
     * 带宽
     */
    private Integer mbps;

    /**
     * 工作温度
     */
    private BigDecimal temperature;
}
