package cn.cyberpeace.mp.situationaware.BO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FlowDetailInfo {

    @ApiModelProperty(value = "流量类型")
    private String protocol;

    @ApiModelProperty(value = "流量产生时间")
    private LocalDateTime time;

    @ApiModelProperty(value = "源ip")
    private String sourceIp;

    @ApiModelProperty(value = "目标ip")
    private String targetIp;

    @ApiModelProperty(value = "源端口")
    private String sourcePort;

}
