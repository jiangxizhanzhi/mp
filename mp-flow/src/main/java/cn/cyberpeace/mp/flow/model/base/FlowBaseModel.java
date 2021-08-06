package cn.cyberpeace.mp.flow.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 流量基础对象
 * @Autho qiaokang
 * @Date：2021/6/17 10:29
 */
@Data
public class FlowBaseModel {

    @ApiModelProperty("首次执行延迟时间，单位：秒(可以为0)")
    private Long delay = 0L;

    @ApiModelProperty("周期执行间隔，单位：秒")
    private Long interval = 1L;

    @ApiModelProperty("执行次数")
    private Integer count = 1;

}
