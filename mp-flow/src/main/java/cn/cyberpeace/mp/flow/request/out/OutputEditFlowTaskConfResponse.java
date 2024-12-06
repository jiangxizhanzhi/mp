package cn.cyberpeace.mp.flow.request.out;

import cn.cyberpeace.mp.flow.bo.FlowTaskAttackConf;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: 修改流量任务配置请求
 * @Author: guquan
 * @Date：2023/5/9 19:01
 */
@Data
@ApiModel("修改流量任务配置请求")
public class OutputEditFlowTaskConfResponse {
    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty("背景流量时长(单位s)")
    private Integer duration;

    @ApiModelProperty("背景流量速率(单位 mbp/s)")
    private Integer rate;

    @ApiModelProperty(value = "攻击流量参数配置")
    private List<FlowTaskAttackConf> attackConfs;
}
