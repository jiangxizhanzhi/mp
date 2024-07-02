package cn.cyberpeace.mp.cscene.request.element;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName ElementMonitorRequest
 * @Description
 * @Author hewei
 * @Date 2022/5/18 15:46
 * @Version 1.0.0
 * @see BaseSceneElementPageRequest
 */
@Data
public class ElementMonitorRequest extends BaseSceneElementPageRequest {
    @ApiModelProperty(value = "rule级别集合 范围 0 ~ 15")
    private List<Integer> levels;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;
}
