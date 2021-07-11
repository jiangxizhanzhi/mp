package cn.cyberpeace.mp.situationaware.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("系统监控事件变更查询条件")
public class WazuhMonitorEventsRequest {
    
    @ApiModelProperty("预警消息默认展示100条")
    private Integer pageSize = 100;

    
    @ApiModelProperty("分页页码")
    private Integer pageNum = 1;

    
    @ApiModelProperty(value = "macId集合", required = false)
    private List<String> macIds;

    @ApiModelProperty(value = "agentName列表", required = false)
    private List<String> agentNames;

    @ApiModelProperty(value = "rule级别集合 范围 0 ~ 15")
    private List<Integer> levels;

    
    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    
    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;
}
