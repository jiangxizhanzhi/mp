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
public class FlowDataStatistic {

    @ApiModelProperty(notes = "x轴-时间")
    private String[] xAxis;

    @ApiModelProperty(notes = "流量大小")
    private int[] value;



}
