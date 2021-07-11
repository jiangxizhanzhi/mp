package cn.cyberpeace.mp.cscene.request.event;

import cn.cyberpeace.mp.cscene.request.common.BaseTimeRangeRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class EventTrendRequest extends BaseTimeRangeRequest {

    @ApiModelProperty("分段数，默认10段")
    private Integer segmentCount;

}
