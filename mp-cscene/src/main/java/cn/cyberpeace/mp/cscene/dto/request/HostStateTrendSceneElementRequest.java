package cn.cyberpeace.mp.cscene.dto.request;

import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description: 资源使用请求体
 * @Author zhouxinghuai
 * @Date：2021/11/24 14:22
 */
@Data
public class HostStateTrendSceneElementRequest extends BaseSceneElementRequest {

    @ApiModelProperty("开始时间, 默认当前时间往前推30分钟")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间，默认当前时间")
    private LocalDateTime endTime;

    @ApiModelProperty("分段数，默认10段")
    private Integer segmentCount;

}
