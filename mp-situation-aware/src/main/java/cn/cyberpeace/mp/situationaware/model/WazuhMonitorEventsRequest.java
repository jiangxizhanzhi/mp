package cn.cyberpeace.mp.situationaware.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@ApiModel("系统监控事件变更查询条件")
public class WazuhMonitorEventsRequest {
    /**
     * 查询条数默认限制
     */
    @ApiModelProperty("预警消息默认展示500条")
    private Integer pageSize = 500;

    /**
     * 分页页码
     */
    @ApiModelProperty("分页页码")
    private Integer pageNum = 1;

    /**
     * wazuh macIds
     */
    @ApiModelProperty(value = "macId集合", required = false)
    private List<String> macIds;

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
