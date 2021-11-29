package cn.cyberpeace.mp.cscene.dto.flow;

import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 17:58
 */
@Data
@Builder
public class FlowTrendSceneElementRequest extends BaseSceneElementRequest {

    @ApiModelProperty("开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty("分段数")
    private Integer segmentCount;

}
