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
public class TeamFlowInfo {

    @ApiModelProperty(value = "战队id")
    private String teamId;

    @ApiModelProperty(value = "流量类型")
    private String protocol;

    @ApiModelProperty(value = "流量产生时间")
    private LocalDateTime time;

    @ApiModelProperty(value = "源ip")
    private String sourceIp;

    @ApiModelProperty(value = "目的ip")
    private String targetIp;

    @ApiModelProperty(value = "源端口")
    private String sourcePort;

    @ApiModelProperty(value = "流量大小")
    private Long size;

    @ApiModelProperty(value = "数据id")
    private String dataId;
}
