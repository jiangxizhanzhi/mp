package cn.cyberpeace.mp.devopscluster.converter.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HypervisorBO {

    /**
     * 节点名称
     */
    @ApiModelProperty(value = "节点名称")
    private String name;

    /**
     * 节点IP
     */
    @ApiModelProperty(value = "节点IP")
    private String ip;

    /**
     * 服务状态
     */
    @ApiModelProperty(value = "服务状态")
    private String status;

    /**
     * 节点管理
     */
    @ApiModelProperty(value = "节点管理")
    private String state;

    /**
     * CPU核数
     */
    @ApiModelProperty(value = "CPU核数")
    private Integer cpu;

    /**
     * 内存大小
     */
    @ApiModelProperty(value = "内存大小")
    private Long ram;

    /**
     * 磁盘大小
     */
    @ApiModelProperty(value = "磁盘大小")
    private Long disk;

    /**
     * 已使用CPU核数
     */
    @ApiModelProperty(value = "已使用CPU核数")
    private Integer cpuUsed;

    /**
     * 已使用内存大小
     */
    @ApiModelProperty(value = "已使用内存大小")
    private Long ramUsed;

    /**
     * 已使用磁盘大小
     */
    @ApiModelProperty(value = "已使用磁盘大小")
    private Long diskUsed;

    /**
     * CPU使用率
     */
    @ApiModelProperty(value = "CPU使用率")
    private BigDecimal cpuPercent;

    /**
     * 内存使用率
     */
    @ApiModelProperty(value = "内存使用率")
    private BigDecimal ramPercent;

    /**
     * 磁盘使用率
     */
    @ApiModelProperty(value = "磁盘使用率")
    private BigDecimal diskPercent;

    /**
     * 虚拟机数量
     */
    @ApiModelProperty(value = "虚拟机数量")
    private Integer totalInstances;

    /**
     * 容器数量
     */
    @ApiModelProperty(value = "容器数量")
    private Integer totalContainers;

    /**
     * 权重
     */
    @ApiModelProperty(value = "权重")
    private Integer weight;

    /**
     * 虚拟机数量限制
     */
    @ApiModelProperty(value = "虚拟机数量限制")
    private Integer maxInstances;

    /**
     * 服务状态
     */
    @ApiModelProperty(value = "服务状态")
    private boolean serviceStatus;
}
