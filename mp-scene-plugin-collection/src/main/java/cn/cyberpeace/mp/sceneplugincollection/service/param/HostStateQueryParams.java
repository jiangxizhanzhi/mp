package cn.cyberpeace.mp.sceneplugincollection.service.param;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class HostStateQueryParams extends QueryParams {

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "节点id列表")
    private List<String> nodeIds;

    /**
     * 事件查询时间段
     */
    private List<WazuhEventRequest.TimeRange> timeRanges;

}
