package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_task")
public class FlowTaskModel extends BaseModel {

    /**
     * 任务标题
     */
    @ApiModelProperty(value = "任务标题")
    private String title;

    /**
     * 任务描述
     */
    @ApiModelProperty(value = "任务描述")
    @DefaultStr
    private String taskDesc;

    /**
     * 流量id
     */
    @ApiModelProperty(value = "流量id")
    @NotBlank(message = "flowId can not be empty")
    private String flowId;

    /**
     * 流量发生平台：0 通用，1 Trex流量发生器
     */
    @ApiModelProperty(value = "流量发生平台：0 通用，1 Trex流量发生器")
    @DefaultInt(1)
    private int flowPlatform;

    /**
     * 第三方id
     */
    @ApiModelProperty(value = "第三方id")
    @DefaultStr
    private String thirdFlowId;

    /**
     * 第三方文件名称
     */
    @ApiModelProperty(value = "第三方文件名称")
    @DefaultStr
    private String thirdFlowName;

    /**
     * 执行次数
     */
    @ApiModelProperty(value = "执行次数")
    @DefaultInt(1)
    private int count;

    /**
     * 首次执行延迟时间，单位：秒(可以为0)
     */
    @ApiModelProperty(value = "首次执行延迟时间，单位：秒(可以为0)")
    @DefaultInt()
    private int delay;

    /**
     * 周期执行间隔，单位：秒
     */
    @ApiModelProperty(value = "周期执行间隔，单位：秒")
    @DefaultInt(1)
    private int intervalTime;

    /**
     * 源ip范围
     */
    @ApiModelProperty(value = "源ip范围")
    @DefaultStr
    private String originIps;

    /**
     * 目标ip范围
     */
    @ApiModelProperty(value = "目标ip范围")
    @DefaultStr
    private String targetIps;


}
