package cn.cyberpeace.mp.cscene.request.flow;

import cn.cyberpeace.mp.flow.bo.AddFlowTaskFlowInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel(value = "新增流量执行任务详情")
public class AddFlowTaskTaskDetail {

    
    @ApiModelProperty(value = "节点ID")
    private String nodeId;

    
    @ApiModelProperty(value = "流量集合")
    private List<AddFlowTaskFlowInfo> flowInfos;
}
