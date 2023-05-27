package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 节点
 * @Author: guquan
 * @Date：2023/5/27 15:22
 */
@Data
@ApiModel("节点")
public class FlowTaskNode {

    /**
     * 节点id
     */
    @ApiModelProperty(value = "节点id")
    private String nodeId;

    /**
     * 节点名称
     */
    @ApiModelProperty(value = "节点名称")
    private String nodeName;
}
