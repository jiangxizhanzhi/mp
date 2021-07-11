package cn.cyberpeace.mp.devopscluster.vo;

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

    
    @ApiModelProperty("节点名称")
    private String name;

    
    @ApiModelProperty("节点IP")
    private String ip;

    
    @ApiModelProperty("物理主机服务状态")
    private boolean serviceStatus;

    
    @ApiModelProperty("计算节点服务状态")
    private String status;

    
    @ApiModelProperty("节点管理")
    private String state;

    
    @ApiModelProperty("CPU核数")
    private Integer cpu;

    
    @ApiModelProperty("内存大小")
    private Long ram;

    
    @ApiModelProperty("磁盘大小")
    private Long disk;

    
    @ApiModelProperty("已使用CPU核数")
    private Integer cpuUsed;

    
    @ApiModelProperty("已使用内存大小")
    private Long ramUsed;

    
    @ApiModelProperty("已使用磁盘大小")
    private Long diskUsed;

    
    @ApiModelProperty("CPU使用率")
    private BigDecimal cpuPercent;

    
    @ApiModelProperty("内存使用率")
    private BigDecimal ramPercent;

    
    @ApiModelProperty("磁盘使用率")
    private BigDecimal diskPercent;

    
    @ApiModelProperty("虚拟机数量")
    private Integer totalInstances;

    
    @ApiModelProperty("容器数量")
    private Integer totalContainers;

    
    @ApiModelProperty("dynamips数量")
    private Integer totalDynamips;

    
    @ApiModelProperty("权重")
    private Integer weight;

    
    @ApiModelProperty("虚拟机数量限制")
    private Integer maxInstances;

    @ApiModelProperty("下载速度")
    private String downloadSpeed;

    @ApiModelProperty("单个网卡最大下载速度")
    private String maxDownloadSpeed;

    @ApiModelProperty("下行带宽/总带宽占比")
    private Double downloadPercent;

    @ApiModelProperty("上传速度")
    private String uploadSpeed;

    @ApiModelProperty("单个网卡最大上传速度")
    private String maxUploadSpeed;

    @ApiModelProperty("上行带宽/总带宽占比")
    private Double uploadPercent;

    @ApiModelProperty("磁盘读速度 Bytes")
    private Long diskReadBytes;

    @ApiModelProperty("磁盘写速度 Bytes")
    private Long diskWriteBytes;

    @ApiModelProperty("磁盘IO利用率 百分比")
    private Double diskIoPercent;

    @ApiModelProperty("辅助存储 百分比")
    private Double secondaryStoragePercent;

    @ApiModelProperty("辅助存储总量")
    private Long secondaryStorageTotal;

    @ApiModelProperty("VLAN 百分比")
    private Double vlanPercent;

    @ApiModelProperty("VLAN总量")
    private Long vlanTotal;

    @ApiModelProperty("存量IP 百分比")
    private Double freeIpPercent;

    @ApiModelProperty("IP总量")
    private Long ipTotal;



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
