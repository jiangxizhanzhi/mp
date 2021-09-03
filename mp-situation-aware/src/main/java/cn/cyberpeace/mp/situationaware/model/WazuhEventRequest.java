package cn.cyberpeace.mp.situationaware.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel("系统监控事件变更查询条件")
public class WazuhEventRequest {

    /**
     * 查询开始时间
     */
    @ApiModelProperty(value = "开始时间-yyyy-MM-dd HH:mm:ss", required = true)
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    /**
     * 查询
     */
    @ApiModelProperty(value = "结束时间-yyyy-MM-dd HH:mm:ss", required = true)
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

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
}
