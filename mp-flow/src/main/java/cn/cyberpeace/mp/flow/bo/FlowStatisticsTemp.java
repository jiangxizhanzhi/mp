package cn.cyberpeace.mp.flow.bo;

import cn.cyberpeace.mp.flow.model.FlowTaskRecordModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 流量仿真统计临时存放数据
 * @Author: guquan
 * @Date：2023/5/17 15:37
 */
@Data
@ApiModel("流量仿真统计临时存放数据")
public class FlowStatisticsTemp extends FlowTaskRecordModel {

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

}
