package cn.cyberpeace.mp.cscene.dto.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class FlowAnalysisSummary {

    @ApiModelProperty(value = "总流量(GB)")
    private Double totalFlowSize;

    @ApiModelProperty(value = "攻击流量(GB)")
    private Double totalAttackFlowSize;

    @ApiModelProperty(value = "网络连接")
    private Integer netConnSize;

    @ApiModelProperty(value = "带宽占用")
    private Double bandwith;

    @ApiModelProperty(value = "攻击事件")
    private Long attackCount;

}
