package cn.cyberpeace.mp.flow.v2.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/26
 */
@Data
public class ValueBO {
    @ApiModelProperty("时间")
    private String time;
    @ApiModelProperty("数值")
    private Double value;
}
