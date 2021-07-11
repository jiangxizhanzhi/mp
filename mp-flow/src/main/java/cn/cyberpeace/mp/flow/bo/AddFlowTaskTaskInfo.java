package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel(value = "新增流量执行任务详情")
public class AddFlowTaskTaskInfo {

    
    @ApiModelProperty(value = "节点ID")
    private String nodeId;

    
    @ApiModelProperty(value = "节点名称")
    private String nodeName;

    
    @ApiModelProperty(value = "节点IP")
    private String nodeIp;

    
    @ApiModelProperty(value = "网络ID")
    private String networkId;

    
    @ApiModelProperty(value = "流量集合")
    private List<AddFlowTaskFlowInfo> flowInfos;
}
