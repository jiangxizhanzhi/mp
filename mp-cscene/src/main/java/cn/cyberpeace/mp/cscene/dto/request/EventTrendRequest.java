package cn.cyberpeace.mp.cscene.dto.request;

import cn.cyberpeace.mp.cscene.dto.common.BaseTimeRangeRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 采集事件趋势图请求体
 * @Author zhouxinghuai
 * @Date：2022/03/11 14:22
 */
@Data
public class EventTrendRequest extends BaseTimeRangeRequest {

    @ApiModelProperty("分段数，默认10段")
    private Integer segmentCount;

}
