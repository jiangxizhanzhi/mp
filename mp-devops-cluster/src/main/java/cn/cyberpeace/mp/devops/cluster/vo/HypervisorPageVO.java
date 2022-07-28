package cn.cyberpeace.mp.devops.cluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "节点管理分页返回信息")
public class HypervisorPageVO extends BaseVO {

    /**
     * 节点名称
     */
    @ApiModelProperty("节点名称")
    private String name;

    /**
     * 节点IP
     */
    @ApiModelProperty("节点IP")
    private String ip;

    /**
     * 物理主机服务状态
     */
    @ApiModelProperty("物理主机服务状态")
    private boolean serviceStatus;

    /**
     * 计算节点服务状态
     */
    @ApiModelProperty("计算节点服务状态")
    private String status;

    /**
     * 节点管理
     */
    @ApiModelProperty("节点管理")
    private String state;

    /**
     * CPU核数
     */
    @ApiModelProperty("CPU核数")
    private Integer cpu;

    /**
     * 内存大小
     */
    @ApiModelProperty("内存大小")
    private Long ram;

    /**
     * 磁盘大小
     */
    @ApiModelProperty("磁盘大小")
    private Long disk;

    /**
     * 已使用CPU核数
     */
    @ApiModelProperty("已使用CPU核数")
    private Integer cpuUsed;

    /**
     * 已使用内存大小
     */
    @ApiModelProperty("已使用内存大小")
    private Long ramUsed;

    /**
     * 已使用磁盘大小
     */
    @ApiModelProperty("已使用磁盘大小")
    private Long diskUsed;

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
     * 虚拟机数量
     */
    @ApiModelProperty("虚拟机数量")
    private Integer totalInstances;

    /**
     * 容器数量
     */
    @ApiModelProperty("容器数量")
    private Integer totalContainers;

    /**
     * 权重
     */
    @ApiModelProperty("权重")
    private Integer weight;

    /**
     * 虚拟机数量限制
     */
    @ApiModelProperty("虚拟机数量限制")
    private Integer maxInstances;

    @ApiModelProperty("下载速度")
    private String downloadSpeed;

    @ApiModelProperty("上传速度")
    private String uploadSpeed;

    @ApiModelProperty("磁盘读速度 Bytes")
    private Long diskReadBytes;

    @ApiModelProperty("磁盘写速度 Bytes")
    private Long diskWriteBytes;


    public BigDecimal getRamPercent() {
        if (Objects.isNull(ramPercent)) {
            return Objects.isNull(this.ram) || Objects.isNull(this.ramUsed) ?
                    BigDecimal.ZERO : BigDecimal.valueOf(this.ramUsed).divide(BigDecimal.valueOf(this.ram),2, RoundingMode.DOWN).multiply(BigDecimal.valueOf(100));
        } else {
            return ramPercent;
        }

    }

    public BigDecimal getCpuPercent() {
        if (Objects.isNull(cpuPercent)) {
            return Objects.isNull(this.cpu) || Objects.isNull(this.cpuUsed) ?
                    BigDecimal.ZERO : BigDecimal.valueOf(this.cpuUsed).divide(BigDecimal.valueOf(this.cpu),2,RoundingMode.DOWN).multiply(BigDecimal.valueOf(100));

        } else {
            return cpuPercent;
        }
    }

    public BigDecimal getDiskPercent() {
        if (Objects.isNull(diskPercent)) {
            return Objects.isNull(this.disk) || Objects.isNull(this.diskUsed) ?
                    BigDecimal.ZERO : BigDecimal.valueOf(this.diskUsed).divide(BigDecimal.valueOf(this.disk),2,RoundingMode.DOWN).multiply(BigDecimal.valueOf(100));
        } else {
            return diskPercent;
        }
    }
}
