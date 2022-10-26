package cn.cyberpeace.mp.flow.v2.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/26
 */
@Data
public class FlowBandwidthCountBO {
    @ApiModelProperty("流量id")
    private String flowId;

    @ApiModelProperty("带宽值")
    private List<ValueBO> values;

    @ApiModelProperty("百分比值")
    private List<ValueBO> percentages;

}
