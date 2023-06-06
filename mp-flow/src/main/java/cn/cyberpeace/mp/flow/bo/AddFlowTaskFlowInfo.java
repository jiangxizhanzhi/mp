package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: 新增流量任务流量详情
 * @Author: guquan
 * @Date：2023/6/6 15:55
 */
@Data
@ApiModel(value = "新增流量任务流量详情")
public class AddFlowTaskFlowInfo {

    /**
     * 流量id
     */
    @ApiModelProperty(value = "流量id")
    private String flowId;

    /**
     * 攻击流量参数配置
     */
    @ApiModelProperty(value = "攻击流量参数配置")
    private List<FlowTaskAttackConf> attackConfs;
}
