package cn.cyberpeace.mp.front.scene.dto.event;

import cn.cyberpeace.mp.front.scene.dto.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 17:40
 */
@Data
public class EventPageRequest extends PageRequest {

    @ApiModelProperty(value = "事件类型")
    private String eventType;

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;
}
