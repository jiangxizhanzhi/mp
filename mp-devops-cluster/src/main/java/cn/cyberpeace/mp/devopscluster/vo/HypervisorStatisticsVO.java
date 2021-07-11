package cn.cyberpeace.mp.devopscluster.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "资源统计返回信息")
public class HypervisorStatisticsVO extends BaseVO {

    
    @ApiModelProperty(value = "CPU核数")
    @JsonIgnore
    private Integer cpus;

    
    @ApiModelProperty(value = "内存大小")
    @JsonIgnore
    private Long memoryMb;

    
    @ApiModelProperty(value = "磁盘大小")
    @JsonIgnore
    private Long localGb;

    
    @ApiModelProperty(value = "已使用CPU核数")
    @JsonIgnore
    private Long cpusUsed;

    
    @ApiModelProperty(value = "已使用内存大小")
    @JsonIgnore
    private Long memoryMbUsed;

    
    @ApiModelProperty(value = "已使用磁盘大小")
    @JsonIgnore
    private Long localGbUsed;

    
    @ApiModelProperty(value = "空闲内存大小")
    @JsonIgnore
    private Long freeRamMb;

    
    @ApiModelProperty(value = "空闲磁盘大小")
    @JsonIgnore
    private Long freeDiskGb;

    
    @ApiModelProperty("CPU使用率")
    @Builder.Default
    private BigDecimal cpuPercent = BigDecimal.ZERO;

    
    @ApiModelProperty("内存使用率")
    @Builder.Default
    private BigDecimal ramPercent = BigDecimal.ZERO;

    
    @ApiModelProperty("磁盘使用率")
    @Builder.Default
    private BigDecimal diskPercent = BigDecimal.ZERO;

    
    @ApiModelProperty(value = "磁盘IO")
    @Builder.Default
    private BigDecimal diskIo = BigDecimal.ZERO;

    
    @ApiModelProperty(value = "网络IO")
    @Builder.Default
    private BigDecimal netIo = BigDecimal.ZERO;

    
    @ApiModelProperty(value = "虚拟机个数")
    @Builder.Default
    private Integer totalInstances = NumberUtils.INTEGER_ZERO;

    
    @ApiModelProperty(value = "容器个数")
    @Builder.Default
    private Integer totalContainers = NumberUtils.INTEGER_ZERO;

    
    @ApiModelProperty(value = "实装设备个数")
    @Builder.Default
    private Integer totalRealEquipments = NumberUtils.INTEGER_ZERO;

    
    @ApiModelProperty(value = "NPC个数")
    @Builder.Default
    private Integer totalNpc = NumberUtils.INTEGER_ZERO;

    
    @ApiModelProperty(value = "交换机个数")
    @Builder.Default
    private Integer totalSwitch = NumberUtils.INTEGER_ZERO;

    
    @ApiModelProperty(value = "网桥个数")
    @Builder.Default
    private Integer totalBridge = NumberUtils.INTEGER_ZERO;

    
    @ApiModelProperty(value = "路由器个数")
    @Builder.Default
    private Integer totalRouters = NumberUtils.INTEGER_ZERO;

    
    @ApiModelProperty(value = "防火墙个数")
    @Builder.Default
    private Integer totalFireWall = NumberUtils.INTEGER_ZERO;

    
    @ApiModelProperty(value = "防火墙个数")
    @Builder.Default
    private Integer totalDynamips = NumberUtils.INTEGER_ZERO;

    public HypervisorStatisticsVO build(Integer cpus, Long memoryMb, Long localGb,
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
        return Objects.isNull(this.memoryMb) || memoryMb == 0 || Objects.isNull(this.memoryMbUsed) ?
                BigDecimal.ZERO : BigDecimal.valueOf(this.memoryMbUsed).divide(BigDecimal.valueOf(this.memoryMb),2, RoundingMode.DOWN).multiply(BigDecimal.valueOf(100));

    }

    public BigDecimal getCpuPercent() {
        return  Objects.isNull(this.cpus) || cpus == 0 || Objects.isNull(this.cpusUsed) ?
                BigDecimal.ZERO : BigDecimal.valueOf(this.cpusUsed).divide(BigDecimal.valueOf(this.cpus),2,RoundingMode.DOWN).multiply(BigDecimal.valueOf(100));
    }

    public BigDecimal getDiskPercent() {
        return  Objects.isNull(this.localGb) || localGb == 0 || Objects.isNull(this.localGbUsed) ?
                BigDecimal.ZERO : BigDecimal.valueOf(this.localGbUsed).divide(BigDecimal.valueOf(this.localGb),2,RoundingMode.DOWN).multiply(BigDecimal.valueOf(100));
    }
}
