package cn.cyberpeace.mp.situationaware.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BaseEventRequest {
    @ApiModelProperty(value = "开始时间-yyyy-MM-dd HH:mm:ss", required = true)
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间-yyyy-MM-dd HH:mm:ss", required = true)
    private LocalDateTime endTime;

    @ApiModelProperty("采集事件默认展示10条")
    private Integer pageSize = 10;

    @ApiModelProperty("分页页码")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "agentName列表")
    private List<String> agentNames;

    @ApiModelProperty(value = "级别集合 范围 0 ~ 15")
    private List<Integer> levels;

    @ApiModelProperty(value = "是否时间降序,默认true")
    private boolean desc = true;
}
