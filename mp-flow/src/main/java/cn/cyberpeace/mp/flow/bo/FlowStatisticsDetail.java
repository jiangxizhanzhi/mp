package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 流量仿真统计详情
 * @Author: guquan
 * @Date：2023/5/17 10:58
 */
@Data
@ApiModel("流量仿真统计详情")
public class FlowStatisticsDetail {

    /**
     * 统计时间
     */
    @ApiModelProperty(value = "统计时间")
    private String statisticalTime;

    /**
     * 相对于当前时间的偏移值
     */
    @ApiModelProperty(value = "相对于当前时间的偏移值")
    private Integer offsetValue;

    /**
     * 相对于当前时间的偏移单位(h:小时 m:分钟 s:秒)
     */
    @ApiModelProperty(value = "相对于当前时间的偏移单位")
    private String offsetUnit;

    /**
     * 统计值
     */
    @ApiModelProperty(value = "统计值")
    private Long value;
}
