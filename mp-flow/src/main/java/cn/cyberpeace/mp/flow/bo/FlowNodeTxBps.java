package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("节点实时速率对象")
public class FlowNodeTxBps {

    
    @ApiModelProperty(value = "节点名称")
    private String nodeName;

    
    @ApiModelProperty(value = "实时发送速率(单位byte/s)")
    private Long txBps;

}
