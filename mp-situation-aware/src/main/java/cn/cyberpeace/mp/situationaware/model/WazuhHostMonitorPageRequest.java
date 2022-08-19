package cn.cyberpeace.mp.situationaware.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("系统监控事件变更查询分页条件")
public class WazuhHostMonitorPageRequest {
    /**
     * 查询条数默认限制
     */
    @ApiModelProperty("预警消息默认展示100条")
    private Integer pageSize = 100;

    /**
     * 分页页码
     */
    @ApiModelProperty("分页页码")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "agentName列表", required = false)
    private List<String> agentNames;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;
}
