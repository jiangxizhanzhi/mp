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

    
    @ApiModelProperty(value = "节点名称")
    private String name;

    
    @ApiModelProperty(value = "节点IP")
    private String ip;

    
    @ApiModelProperty(value = "服务状态")
    private String status;

    
    @ApiModelProperty(value = "节点管理")
    private String state;

    
    @ApiModelProperty(value = "CPU核数")
    private Integer cpu;

    
    @ApiModelProperty(value = "内存大小")
    private Long ram;

    
    @ApiModelProperty(value = "磁盘大小")
    private Long disk;

    
    @ApiModelProperty(value = "已使用CPU核数")
    private Integer cpuUsed;

    
    @ApiModelProperty(value = "已使用内存大小")
    private Long ramUsed;

    
    @ApiModelProperty(value = "已使用磁盘大小")
    private Long diskUsed;

    
    @ApiModelProperty(value = "CPU使用率")
    private BigDecimal cpuPercent;

    
    @ApiModelProperty(value = "内存使用率")
    private BigDecimal ramPercent;

    
    @ApiModelProperty(value = "磁盘使用率")
    private BigDecimal diskPercent;

    
    @ApiModelProperty(value = "虚拟机数量")
    private Integer totalInstances;

    
    @ApiModelProperty(value = "容器数量")
    private Integer totalContainers;

    
    @ApiModelProperty(value = "权重")
    private Integer weight;

    
    @ApiModelProperty(value = "虚拟机数量限制")
    private Integer maxInstances;

    
    @ApiModelProperty(value = "服务状态")
    private boolean serviceStatus;
}
