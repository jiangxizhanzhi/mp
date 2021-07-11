package cn.cyberpeace.mp.cscene.dto.element;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "节点状态信息(cpu，内存，磁盘等")
public class HostState {

    @ApiModelProperty("场景id")
    private String sceneId;

    @ApiModelProperty("节点id")
    private String elementId;

    @ApiModelProperty("探针名称")
    private String agentName;

    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("总内存(MB)")
    private Long memoryTotal;

    @ApiModelProperty("空闲内存(MB)")
    private Long memoryFree;

    @ApiModelProperty("内存使用率")
    private Double memoryPercent;

    @ApiModelProperty("cpu主频")
    private Double cpuFreq;

    @ApiModelProperty("cpu使用率")
    private Double cpuPercent;

    @ApiModelProperty("CPU核数")
    private Integer cpuCount;

    @ApiModelProperty("磁盘空间(GB)")
    private Long diskSpace;

    
    

    @ApiModelProperty("磁盘未使用空间")
    private Long diskUnused;

    @ApiModelProperty("磁盘使用率 百分比")
    private Double diskPercent;

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

    @ApiModelProperty("最大网卡速率")
    private Long maxNetSpeed;

    @ApiModelProperty("磁盘读速度 Bytes")
    private Long diskReadBytes;

    @ApiModelProperty("磁盘写速度 Bytes")
    private Long diskWriteBytes;

    @ApiModelProperty("磁盘IO利用率 百分比")
    private Double diskIoPercent;
}
