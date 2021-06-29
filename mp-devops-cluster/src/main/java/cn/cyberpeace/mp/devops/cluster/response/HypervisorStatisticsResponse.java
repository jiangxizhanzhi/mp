package cn.cyberpeace.mp.devops.cluster.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class HypervisorStatisticsResponse extends BaseCloudPlatformResponse {

    /**
     * CPU核数
     */
    @ApiModelProperty(value = "CPU核数")
    @JsonIgnore
    private Integer cpus;

    /**
     * 内存大小
     */
    @ApiModelProperty(value = "内存大小")
    @JsonIgnore
    private Long memoryMb;

    /**
     * 磁盘总大小
     */
    @ApiModelProperty(value = "磁盘大小")
    @JsonIgnore
    private Long localGb;

    /**
     * 已使用CPU核数
     */
    @ApiModelProperty(value = "已使用CPU核数")
    @JsonIgnore
    private Long cpusUsed;

    /**
     * 已使用内存大小
     */
    @ApiModelProperty(value = "已使用内存大小")
    @JsonIgnore
    private Long memoryMbUsed;

    /**
     * 已使用磁盘大小
     */
    @ApiModelProperty(value = "已使用磁盘大小")
    @JsonIgnore
    private Long localGbUsed;

    /**
     * 空闲内存大小
     */
    @ApiModelProperty(value = "空闲内存大小")
    @JsonIgnore
    private Long freeRamMb;

    /**
     * 空闲磁盘大小
     */
    @ApiModelProperty(value = "空闲磁盘大小")
    @JsonIgnore
    private Long freeDiskGb;

    /**
     * CPU使用率
     */
    @ApiModelProperty("CPU使用率")
    @Builder.Default
    private BigDecimal cpuPercent = BigDecimal.ZERO;

    /**
     * 内存使用率
     */
    @ApiModelProperty("内存使用率")
    @Builder.Default
    private BigDecimal ramPercent = BigDecimal.ZERO;

    /**
     * 磁盘使用率
     */
    @ApiModelProperty("磁盘使用率")
    @Builder.Default
    private BigDecimal diskPercent = BigDecimal.ZERO;

    /**
     * 磁盘IO
     */
    @ApiModelProperty(value = "磁盘IO")
    @Builder.Default
    private BigDecimal diskIo = BigDecimal.ZERO;

    /**
     * 网络IO
     */
    @ApiModelProperty(value = "网络IO")
    @Builder.Default
    private BigDecimal netIo = BigDecimal.ZERO;

    /**
     * 虚拟机个数
     */
    @ApiModelProperty(value = "虚拟机个数")
    @Builder.Default
    private Integer totalInstances = 0;

    /**
     * 容器个数
     */
    @ApiModelProperty(value = "容器个数")
    @Builder.Default
    private Integer totalContainers = 0;

    /**
     * 实装设备个数
     */
    @ApiModelProperty(value = "实装设备个数")
    @Builder.Default
    private Integer totalRealEquipments = 0;

    /**
     * NPC个数
     */
    @ApiModelProperty(value = "NPC个数")
    @Builder.Default
    private Integer totalNpc = 0;

    /**
     * 交换机个数
     */
    @ApiModelProperty(value = "交换机个数")
    @Builder.Default
    private Integer totalSwitch = 0;

    /**
     * 网桥个数
     */
    @ApiModelProperty(value = "网桥个数")
    @Builder.Default
    private Integer totalBridge = 0;

    /**
     * 路由器个数
     */
    @ApiModelProperty(value = "路由器个数")
    @Builder.Default
    private Integer totalRouters = 0;

    /**
     * 防火墙个数
     */
    @ApiModelProperty(value = "防火墙个数")
    @Builder.Default
    private Integer totalFireWall = 0;

    public HypervisorStatisticsResponse HypervisorStatisticsResponse(Integer cpus, Long memoryMb, Long localGb,
                                        Long cpusUsed, Long memoryMbUsed, Long localGbUsed,
                                        Long freeRamMb, Long freeDiskGb) {
        this.cpus = cpus;
        this.memoryMb = memoryMb;
        this.localGb = localGb;
        this.cpusUsed = cpusUsed;
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
        return BigDecimal.valueOf(this.cpusUsed).divide(BigDecimal.valueOf(this.cpus),2,RoundingMode.DOWN).multiply(BigDecimal.valueOf(100));
    }

    public BigDecimal getDiskPercent() {
        return BigDecimal.valueOf(this.localGbUsed).divide(BigDecimal.valueOf(this.localGb),2,RoundingMode.DOWN).multiply(BigDecimal.valueOf(100));
    }
}
