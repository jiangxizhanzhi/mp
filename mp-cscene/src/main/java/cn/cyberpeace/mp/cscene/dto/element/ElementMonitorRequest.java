package cn.cyberpeace.mp.cscene.dto.element;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description:
 * @Author hewei
 * @Date：2022/5/18 15:46
 */
@Data
public class ElementMonitorRequest extends BaseSceneElementPageRequest {

    @ApiModelProperty("开始时间")
    private LocalDateTime localStartTime;

    @ApiModelProperty("结束时间")
    private LocalDateTime localEndTime;

    @ApiModelProperty(value = "rule级别集合 范围 0 ~ 15")
    private List<Integer> levels;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;
}
