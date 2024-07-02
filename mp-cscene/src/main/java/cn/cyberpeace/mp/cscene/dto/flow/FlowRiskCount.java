package cn.cyberpeace.mp.cscene.dto.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FlowRiskCount
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/11/22 17:58
 * @Version 1.0.0
 */
@Data
public class FlowRiskCount {

    @ApiModelProperty(notes = "高风险")
    private int highCount;

    @ApiModelProperty(notes = "中风险")
    private int mediumCount;

    @ApiModelProperty(notes = "中风险")
    private int lowCount;

}
