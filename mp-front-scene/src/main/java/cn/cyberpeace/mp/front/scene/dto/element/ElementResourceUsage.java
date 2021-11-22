package cn.cyberpeace.mp.front.scene.dto.element;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 18:09
 */
@Data
public class ElementResourceUsage {

    @ApiModelProperty(notes = "x轴-时间")
    private String[] xAxis;

    @ApiModelProperty(notes = "cpu使用率")
    private float[] cpu;


    @ApiModelProperty(notes = "内存使用率")
    private float[] mem;

}
