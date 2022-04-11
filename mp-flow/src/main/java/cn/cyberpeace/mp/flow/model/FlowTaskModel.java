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
import javax.validation.constraints.Pattern;

/**
 * @Description: 流量任务对象
 * @Author qiaokang
 * @Date：2021/8/31 17:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_task")
public class FlowTaskModel extends BaseModel {

    /**
     * 任务标题
     */
    @ApiModelProperty(value = "任务标题")
    @Pattern(regexp = "^[a-zA-Z0-9_\u4e00-\u9fa5]+$", message = "标题只能包含中英文数字及下划线")
    private String title;

    /**
     * 任务描述
     */
    @ApiModelProperty(value = "任务描述")
    @DefaultStr
    private String taskDesc;

    /**
     * 流量执行器id
     */
    @ApiModelProperty(value = "流量执行器id")
    @NotBlank(message = "actuatorId can not be empty")
    private String actuatorId;

    /**
     * 流量id
     */
    @ApiModelProperty(value = "流量id")
    @DefaultStr
    private String flowId;

    /**
     * 流量网口id
     */
    @ApiModelProperty(value = "流量网口id")
    @DefaultStr
    private String flowPortId;

    /**
     * 流量发生平台：0 通用，1 Trex流量发生器
     */
    @ApiModelProperty(value = "流量发生平台：0 通用，1 Trex流量发生器")
    @DefaultInt(1)
    private Integer flowPlatform;

    /**
     * 流量第三方id
     */
    @ApiModelProperty(value = "流量第三方id")
    @DefaultStr
    private String thirdFlowId;

    /**
     * 第三方文件名称
     */
    @ApiModelProperty(value = "第三方文件名称")
    @DefaultStr
    private String thirdFlowName;

    /**
     * 流量任务第三方id
     */
    @ApiModelProperty(value = "流量任务第三方id")
    @DefaultStr
    private String thirdTaskId;

    /**
     * 执行次数
     */
    @ApiModelProperty(value = "执行次数")
    @DefaultInt(1)
    private Integer count;

    /**
     * 首次执行延迟时间，单位：秒(可以为0)
     */
    @ApiModelProperty(value = "首次执行延迟时间，单位：秒(可以为0)")
    @DefaultInt()
    private Integer delay;

    /**
     * 周期执行间隔，单位：秒
     */
    @ApiModelProperty(value = "周期执行间隔，单位：秒")
    @DefaultInt(1)
    private Integer intervalTime;

    /**
     * 任务状态: 0 初始化，1 正在运行，2 正在停止， 3 已停止
     */
    @ApiModelProperty(value = "任务状态: 0 初始化，1 正在运行，2 正在停止， 3 已停止")
    @DefaultInt()
    private Integer taskStatus;

    /**
     * 实际运行次数
     */
    @ApiModelProperty(value = "实际运行次数")
    @DefaultInt()
    private Integer runningCount;

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

    /**
     * 自定义流量任务标志 0：否， 1：是
     */
    @ApiModelProperty(value = "自定义流量任务标志 0：否， 1：是")
    @DefaultInt()
    private Integer customFlag;

    /**
     * 流量协议
     */
    @ApiModelProperty(value = "流量协议")
    @DefaultStr
    private String flowProtocol;

    /**
     * 流量内容
     */
    @ApiModelProperty(value = "流量内容")
    @DefaultStr
    private String content;

}
