package cn.cyberpeace.mp.base.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
}
