package cn.cyberpeace.mp.flow.v2.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/26
 */
@Data
public class FlowDashBoardCountBO {
    @ApiModelProperty("统计结果")
    private Long count;
}
