package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("节点")
public class FlowTaskNode {

    
    @ApiModelProperty(value = "节点id")
    private String nodeId;

    
    @ApiModelProperty(value = "节点名称")
    private String nodeName;
}
