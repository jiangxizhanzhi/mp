package cn.cyberpeace.mp.flow.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CsceneInstanceFlowTaskResponse {
    @ApiModelProperty(value = "任务id")
    private String taskId;
    @ApiModelProperty(value = "场景实例id")
    private String csceneInstanceId;
    @ApiModelProperty(value = "场景节点id")
    private String csceneNodeId;
    @ApiModelProperty(value = "流量id")
    private String flowId;
    @ApiModelProperty(value = "流量名称")
    private String name;
    @ApiModelProperty(value = "交互类型：0 背景流量，1 攻击流量")
    private Integer interactiveType;
    @ApiModelProperty(value = "流量资源类型:0 pcap，1 脚本")
    private Integer resourceType;
    @ApiModelProperty("背景流量速率(单位 mbp/s)")
    private Integer rate;
    @ApiModelProperty("带宽(单位 mbp/s)")
    private Integer bandwidth;
    @ApiModelProperty(value = "攻击流量 脚本运行参数")
    private List<ScriptParamModel> scriptParamModels;
}
