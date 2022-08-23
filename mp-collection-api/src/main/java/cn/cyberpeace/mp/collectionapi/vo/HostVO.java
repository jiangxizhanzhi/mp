package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HostVO {
    @ApiModelProperty(value = "CPU占用")
    private Double cpuPercent;
    @ApiModelProperty(value = "磁盘占用")
    private Double diskUsePercent;
    @ApiModelProperty(value = "内存占用")
    private Double memoryUsePercent;
    @ApiModelProperty(value = "agentIp")
    private String agentIp;
    @ApiModelProperty(value = "采集时间")
    private LocalDateTime time;
}
