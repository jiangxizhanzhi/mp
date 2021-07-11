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


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_task")
public class FlowTaskModel extends BaseModel {

    
    @ApiModelProperty(value = "任务标题")
    @Pattern(regexp = "^[a-zA-Z0-9_\u4e00-\u9fa5]+$", message = "标题只能包含中英文数字及下划线")
    private String title;

    
    @ApiModelProperty(value = "任务描述")
    @DefaultStr
    private String taskDesc;

    
    @ApiModelProperty(value = "流量执行器id")
    @NotBlank(message = "actuatorId can not be empty")
    private String actuatorId;

    
    @ApiModelProperty(value = "流量id")
    @DefaultStr
    private String flowId;

    
    @ApiModelProperty(value = "流量网口id")
    @DefaultStr
    private String flowPortId;

    
    @ApiModelProperty(value = "流量发生平台：0 通用，1 Trex流量发生器")
    @DefaultInt(1)
    private Integer flowPlatform;

    
    @ApiModelProperty(value = "流量第三方id")
    @DefaultStr
    private String thirdFlowId;

    
    @ApiModelProperty(value = "第三方文件名称")
    @DefaultStr
    private String thirdFlowName;

    
    @ApiModelProperty(value = "流量任务第三方id")
    @DefaultStr
    private String thirdTaskId;

    
    @ApiModelProperty(value = "执行次数")
    @DefaultInt(1)
    private Integer count;

    
    @ApiModelProperty(value = "首次执行延迟时间，单位：秒(可以为0)")
    @DefaultInt()
    private Integer delay;

    
    @ApiModelProperty(value = "周期执行间隔，单位：秒")
    @DefaultInt(1)
    private Integer intervalTime;

    
    @ApiModelProperty(value = "任务状态: 0 初始化，1 正在运行，2 正在停止， 3 已停止")
    @DefaultInt()
    private Integer taskStatus;

    
    @ApiModelProperty(value = "实际运行次数")
    @DefaultInt()
    private Integer runningCount;

    
    @ApiModelProperty(value = "源ip范围")
    @DefaultStr
    private String originIps;

    
    @ApiModelProperty(value = "目标ip范围")
    @DefaultStr
    private String targetIps;

    
    @ApiModelProperty(value = "自定义流量任务标志 0：否， 1：是")
    @DefaultInt()
    private Integer customFlag;

    
    @ApiModelProperty(value = "流量协议")
    @DefaultStr
    private String flowProtocol;

    
    @ApiModelProperty(value = "流量内容")
    @DefaultStr
    private String content;

    
    @ApiModelProperty(value = "倍数：大于等于1的整数")
    @DefaultInt(1)
    private Integer mult;

    
    @ApiModelProperty(value = "节点id")
    @DefaultStr
    private String nodeId;


}
