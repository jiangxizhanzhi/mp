package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: 新增流量任务请求
 * @Author qiaokang
 * @Date：2021/10/11 16:16
 */
@Data
@ApiModel(value = "新增流量任务请求")
public class AddFlowTaskRequest extends FlowTaskModel {

    /**
     * 网络id
     */
    @ApiModelProperty(value = "网络id")
    private String networkId;

    /**
     * 攻击脚本参数列表
     */
    @ApiModelProperty(value = "攻击脚本运行参数")
    private List<FlowScriptParamModel> scriptParamModels;

}
