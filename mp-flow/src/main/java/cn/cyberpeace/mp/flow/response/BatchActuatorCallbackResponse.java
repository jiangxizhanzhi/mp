package cn.cyberpeace.mp.flow.response;

import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 流量批量执行回调方法返回信息
 * @Author qiaokang
 * @Date：2021/8/10 16:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量批量执行回调方法返回信息")
public class BatchActuatorCallbackResponse {

    /**
     * 执行成功任务列表
     */
    @ApiModelProperty(value = "执行成功任务列表")
    private List<FlowTaskModel> successTaskModels;

    /**
     * 执行失败任务列表
     */
    @ApiModelProperty(value = "执行失败任务列表")
    private List<FlowTaskModel> errorTaskModels;

}
