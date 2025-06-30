package cn.cyberpeace.mp.base.out;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlowInfo {
    @ApiModelProperty(value = "任务id")
    private String taskId;
    @ApiModelProperty(value = "源节点和目标节点的连通性")
    private Boolean connected;
    @ApiModelProperty(value = "丢包率")
    private Double packetLoss;
    @ApiModelProperty(value = "时延")
    private Double delay;
    @ApiModelProperty(value = "实时发送速率(单位byte/s)")
    private Long txBps;
}
