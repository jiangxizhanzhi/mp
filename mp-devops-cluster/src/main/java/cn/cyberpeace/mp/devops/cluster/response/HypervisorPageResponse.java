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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "节点管理分页返回信息")
public class HypervisorPageResponse extends BaseCloudPlatformResponse {

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
     * 服务状态
     */
    @ApiModelProperty("服务状态")
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
    @JsonIgnore
    private Integer cpu;

    /**
     * 内存大小
     */
    @ApiModelProperty("内存大小")
    @JsonIgnore
    private Long ram;

    /**
     * 磁盘大小
     */
    @ApiModelProperty("磁盘大小")
    @JsonIgnore
    private Long disk;

    /**
     * 已使用CPU核数
     */
    @ApiModelProperty("已使用CPU核数")
    @JsonIgnore
    private Integer cpuUsed;

    /**
     * 已使用内存大小
     */
    @ApiModelProperty("已使用内存大小")
    @JsonIgnore
    private Long ramUsed;

    /**
     * 已使用磁盘大小
     */
    @ApiModelProperty("已使用磁盘大小")
    @JsonIgnore
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


    public BigDecimal getRamPercent() {
        return BigDecimal.valueOf(this.ramUsed).divide(BigDecimal.valueOf(this.ram),2, RoundingMode.DOWN).multiply(BigDecimal.valueOf(100));

    }

    public BigDecimal getCpuPercent() {
        return BigDecimal.valueOf(this.cpuUsed).divide(BigDecimal.valueOf(this.cpu),2,RoundingMode.DOWN).multiply(BigDecimal.valueOf(100));
    }

    public BigDecimal getDiskPercent() {
        return BigDecimal.valueOf(this.diskUsed).divide(BigDecimal.valueOf(this.disk),2,RoundingMode.DOWN).multiply(BigDecimal.valueOf(100));
    }
}
