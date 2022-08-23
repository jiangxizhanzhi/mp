package cn.cyberpeace.mp.collection.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HostMonitorBaseRequest {
    @ApiModelProperty(value = "agentName列表")
    private List<String> agentNames;

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;
}
