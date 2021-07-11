package cn.cyberpeace.mp.cscene.request.flow;

import cn.cyberpeace.mp.cscene.request.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class FlowPageRequest extends PageRequest {

    @ApiModelProperty(value = "源主机")
    private String srcHost;

    @ApiModelProperty(value = "目的主机")
    private String destHost;

    @ApiModelProperty(value = "流量类型")
    private String flowType;

    @ApiModelProperty(value = "告警级别 1:低风险 2:中风险 3:高风险")
    private Integer riskRank;

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;

}
