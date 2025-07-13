package cn.cyberpeace.mp.base.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Description: 新增流量执行任务请求
 * @Author: guquan
 * @Date：2023/5/8 10:04
 */
@Data
@ApiModel(value = "新增流量执行任务请求")
public class OutAttackToolRequest {
    @ApiModelProperty(value = "场景实例ID")
    private String id;

    @ApiModelProperty(value = "源节点")
    private String sourceNode;

    @ApiModelProperty(value = "目标节点")
    private String targetNode;

    @ApiModelProperty(value = "使用的工具")
    private String toolId;

    @ApiModelProperty(value = "参数")
    List<ImageToolRequest.Params> params;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "目标id")
    private String targetId;

    @ApiModelProperty(value = "开始攻击时间")
    private Date attackStarttime;

    @ApiModelProperty(value = "攻击结束时间")
    private Date attackEndtime;
}
