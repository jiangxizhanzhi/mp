package cn.cyberpeace.mp.front.scene.dto.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 17:58
 */
@Data
@Builder
public class FlowRiskAggrResponse {

    @ApiModelProperty(notes = "高风险")
    private int highCount;

    @ApiModelProperty(notes = "中风险")
    private int mediumCount;

    @ApiModelProperty(notes = "中风险")
    private int lowCount;

}
