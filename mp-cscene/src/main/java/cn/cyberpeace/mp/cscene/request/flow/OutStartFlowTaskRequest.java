package cn.cyberpeace.mp.cscene.request.flow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 新增流量执行任务请求
 * @Author: guquan
 * @Date：2023/5/8 10:04
 */
@Data
@ApiModel(value = "新增流量执行任务请求")
public class OutStartFlowTaskRequest {
    @ApiModelProperty(value = "场景实例ID")
    private String id;

    @ApiModelProperty(value = "源节点")
    private String sourceNode;

    @ApiModelProperty(value = "目标节点")
    private String targetNode;

    @ApiModelProperty(value = "流量包大小")
    private Integer size;

    @ApiModelProperty(value = "1 视频/2 音频/3 普通数据包/4 邮件/5 聊天")
    private Integer type;

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id")
    private String taskId;

    /**
     * 源id
     */
    @ApiModelProperty(value = "源id")
    private String attackerId;

    /**
     * 目的id
     */
    @ApiModelProperty(value = "目的id")
    private String targetId;
}
