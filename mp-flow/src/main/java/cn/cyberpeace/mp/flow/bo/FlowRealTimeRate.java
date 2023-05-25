package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 实时速率统计对象
 * @Author: guquan
 * @Date：2023/5/16 11:23
 */
@Data
@ApiModel("实时速率统计对象")
public class FlowRealTimeRate {

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id")
    private String taskId;

    /**
     * 统计时间
     */
    @ApiModelProperty(value = "统计时间")
    private String statisticalTime;

    /**
     * 相对于当前时间的秒数
     */
    @ApiModelProperty(value = "相对于当前时间的秒数")
    private Integer second;

    /**
     * 实时速率（单位byte/s）
     */
    @ApiModelProperty(value = "实时速率（单位byte/s）")
    private Double txBps;

}
