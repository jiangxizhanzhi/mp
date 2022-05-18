package cn.cyberpeace.mp.scenepluginflow.plugin.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FlowIdWithSourceIp {
    @ApiModelProperty(value = "流量id")
    private String flowId;
    @ApiModelProperty(value = "源ip")
    private String sourceIp;
}
