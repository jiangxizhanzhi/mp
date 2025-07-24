package cn.cyberpeace.mp.cscene.request.flow;

import cn.cyberpeace.mp.flow.bo.AddFlowTaskFlowInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: guquan
 * @Date：2023/5/29 17:14
 */
@Data
@ApiModel(value = "新增流量执行任务详情")
public class AddFlowTaskTaskDetail {

    /**
     * 节点ID
     */
    @ApiModelProperty(value = "节点ID")
    private String nodeId;

    /**
     * 流量集合
     */
    @ApiModelProperty(value = "流量集合")
    private List<AddFlowTaskFlowInfo> flowInfos;
}
