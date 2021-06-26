package cn.cyberpeace.mp.devops.cluster.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 资源统计信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "资源统计返回信息")
public class HypervisorStatisticsResponse extends BaseOpenStackResponse{

    /**
     * CPU核数
     */
    @ApiModelProperty(value = "CPU核数")
    private Integer vcpus;

    /**
     * 内存大小
     */
    @ApiModelProperty(value = "内存大小")
    private Long memoryMb;

    /**
     * 磁盘总大小
     */
    @ApiModelProperty(value = "磁盘大小")
    private Long localGb;

    /**
     * 已使用CPU核数
     */
    @ApiModelProperty(value = "已使用CPU核数")
    private Long vcpusUsed;

    /**
     * 已使用内存大小
     */
    @ApiModelProperty(value = "已使用内存大小")
    private Long memoryMbUsed;

    /**
     * 已使用磁盘大小
     */
    @ApiModelProperty(value = "已使用磁盘大小")
    private Long localGbUsed;

    /**
     * 空闲内存大小
     */
    @ApiModelProperty(value = "空闲内存大小")
    private Long freeRamMb;

    /**
     * 空闲磁盘大小
     */
    @ApiModelProperty(value = "空闲磁盘大小")
    private Long freeDiskGb;

    /**
     * CPU使用率
     */
    @ApiModelProperty("CPU使用率")
    private BigDecimal cpuPercent;

    /**
     * 内存使用率
     */
    @ApiModelProperty("内存使用率")
    private BigDecimal ramPercent;

    /**
     * 磁盘使用率
     */
    @ApiModelProperty("磁盘使用率")
    private BigDecimal diskPercent;

    /**
     * 磁盘IO
     */
    @ApiModelProperty(value = "磁盘IO")
    private BigDecimal diskIo;

    /**
     * 网络IO
     */
    @ApiModelProperty(value = "网络IO")
    private BigDecimal netIo;

    /**
     * 虚拟机个数
     */
    @ApiModelProperty(value = "虚拟机个数")
    private Integer totalInstances;

    /**
     * 容器个数
     */
    @ApiModelProperty(value = "容器个数")
    private Integer totalContainers;

    /**
     * 实装设备个数
     */
    @ApiModelProperty(value = "实装设备个数")
    private Integer totalRealEquipments;

    /**
     * NPC个数
     */
    @ApiModelProperty(value = "NPC个数")
    private Integer totalNpc;

    /**
     * 交换机个数
     */
    @ApiModelProperty(value = "交换机个数")
    private Integer totalSwitch;

    /**
     * 网桥个数
     */
    @ApiModelProperty(value = "网桥个数")
    private Integer totalBridge;

    /**
     * 路由器个数
     */
    @ApiModelProperty(value = "路由器个数")
    private Integer totalRouters;


    public HypervisorStatisticsResponse HypervisorStatisticsResponse(Integer vcpus, Long memoryMb, Long localGb,
                                        Long vcpusUsed, Long memoryMbUsed, Long localGbUsed,
                                        Long freeRamMb, Long freeDiskGb) {
        this.vcpus = vcpus;
        this.memoryMb = memoryMb;
        this.localGb = localGb;
        this.vcpusUsed = vcpusUsed;
        this.memoryMbUsed = memoryMbUsed;
        this.localGbUsed = localGbUsed;
        this.freeRamMb = freeRamMb;
        this.freeDiskGb = freeDiskGb;
        return this;
    }

    public BigDecimal getRamPercent() {
        return BigDecimal.valueOf(this.memoryMbUsed).divide(BigDecimal.valueOf(this.memoryMb),2, RoundingMode.DOWN).multiply(BigDecimal.valueOf(100));

    }

    public BigDecimal getCpuPercent() {
        return BigDecimal.valueOf(this.vcpusUsed).divide(BigDecimal.valueOf(this.vcpus),2,RoundingMode.DOWN).multiply(BigDecimal.valueOf(100));
    }

    public BigDecimal getDiskPercent() {
        return BigDecimal.valueOf(this.localGbUsed).divide(BigDecimal.valueOf(this.localGb),2,RoundingMode.DOWN).multiply(BigDecimal.valueOf(100));
    }
}
