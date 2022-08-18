package cn.cyberpeace.mp.situationaware.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HostBO {
    @ApiModelProperty(value = "CPU占用")
    private Double CPUPercent;
    @ApiModelProperty(value = "磁盘占用")
    private Double diskUsePercent;
    @ApiModelProperty(value = "内存占用")
    private Double memoryUsePercent;
    @ApiModelProperty(value = "agentIp")
    private String agentIp;
}
