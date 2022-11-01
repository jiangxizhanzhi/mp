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
public class FlowGeneratorBandwidthCountBO {
    @ApiModelProperty("流量执行器id")
    private String flowGeneratorId;

    @ApiModelProperty("实时总带宽")
    private Long total;

    @ApiModelProperty("仿真任务带宽统计")
    private List<FlowTaskBandwidthCountBO> tasks;
}
