package cn.cyberpeace.mp.cscene.dto.request;

import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @Description: 资源使用请求体
 * @Author zhouxinghuai
 * @Date：2021/11/24 14:22
 */
@Data
public class HostStateTrendRequest extends BaseSceneElementRequest {

    @ApiModelProperty("开始时间, 默认当前时间往前推30分钟")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间，默认当前时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @ApiModelProperty("分段数，默认10段")
    private Integer segmentCount;

}
