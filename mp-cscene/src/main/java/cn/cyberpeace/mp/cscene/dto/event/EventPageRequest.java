package cn.cyberpeace.mp.cscene.dto.event;

import cn.cyberpeace.mp.cscene.dto.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 17:40
 */
@Data
public class EventPageRequest extends PageRequest {

    @ApiModelProperty(value = "场景id")
    private String sceneId;

    @ApiModelProperty(value = "节点id")
    private List<String> elementIds;

    @ApiModelProperty(value = "事件类型")
    private String eventType;

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;
}
