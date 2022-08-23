package cn.cyberpeace.mp.collection.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowDetailInfo {
    @ApiModelProperty(value = "队伍id")
    private String teamId;

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

    @ApiModelProperty(value = "目标端口")
    private String targetPort;

    @ApiModelProperty(value = "源mac")
    private String sourceMac;

    @ApiModelProperty(value = "目标mac")
    private String targetMac;

    @ApiModelProperty(value = "流量大小")
    private Long size;

}
