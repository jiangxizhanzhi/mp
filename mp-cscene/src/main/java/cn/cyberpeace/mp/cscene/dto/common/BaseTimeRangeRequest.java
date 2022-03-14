package cn.cyberpeace.mp.cscene.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @Description: 基础时间范围请求
 * @Author zhouxinghuai
 * @Date：2022/03/14 14:22
 */
@Data
public class BaseTimeRangeRequest extends BaseSceneElementRequest {

    @ApiModelProperty("开始时间, 默认当前时间往前推30分钟")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间，默认当前时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

}
