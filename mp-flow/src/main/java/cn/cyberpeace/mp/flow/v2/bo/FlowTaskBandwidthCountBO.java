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
public class FlowTaskBandwidthCountBO {
    @ApiModelProperty("流量任务id")
    private String flowTaskId;

    @ApiModelProperty("实时总带宽")
    private Long total;

    @ApiModelProperty("带宽值")
    private List<ValueBO> values;

    @ApiModelProperty("百分比值")
    private List<ValueBO> percentages;

    @ApiModelProperty("流量带宽统计")
    private List<FlowBandwidthCountBO> flows;
}
