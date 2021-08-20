package cn.cyberpeace.mp.flow.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 流量基础对象
 * @Autho qiaokang
 * @Date：2021/6/17 10:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowBaseModel {

    @ApiModelProperty("首次执行延迟时间，单位：秒(可以为0)")
    private int delay = 0;

    @ApiModelProperty("周期执行间隔，单位：秒")
    private int interval = 1;

    @ApiModelProperty("执行次数")
    private int count = 1;

}
