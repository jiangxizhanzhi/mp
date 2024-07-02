package cn.cyberpeace.mp.cscene.request.event;

import cn.cyberpeace.mp.cscene.request.common.BaseTimeRangeRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName EventTrendRequest
 * @Description 采集事件趋势图请求体
 * @Author zhouxinghuai
 * @Date 2022/03/11 14:22
 * @Version 1.0.0
 * @see BaseTimeRangeRequest
 */
@Data
public class EventTrendRequest extends BaseTimeRangeRequest {

    @ApiModelProperty("分段数，默认10段")
    private Integer segmentCount;

}
