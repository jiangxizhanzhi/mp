package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

/**
 * @ClassName FlowTaskNode
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
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

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, nodeName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowTaskNode that = (FlowTaskNode) o;
        return Objects.equals(nodeId, that.nodeId) && Objects.equals(nodeName, that.nodeName);
    }
}
