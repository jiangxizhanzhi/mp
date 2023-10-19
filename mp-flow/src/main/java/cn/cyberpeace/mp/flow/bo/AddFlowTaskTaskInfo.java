package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: 新增流量执行任务详情
 * @Author: guquan
 * @Date：2023/5/31 9:38
 */
@Data
@ApiModel(value = "新增流量执行任务详情")
public class AddFlowTaskTaskInfo {

    /**
     * 节点ID
     */
    @ApiModelProperty(value = "节点ID")
    private String nodeId;

    /**
     * 节点名称
     */
    @ApiModelProperty(value = "节点名称")
    private String nodeName;

    /**
     * 节点IP
     */
    @ApiModelProperty(value = "节点IP")
    private String nodeIp;

    /**
     * 网络ID
     */
    @ApiModelProperty(value = "网络ID")
    private String networkId;

    /**
     * 流量集合
     */
    @ApiModelProperty(value = "流量集合")
    private List<AddFlowTaskFlowInfo> flowInfos;
}
