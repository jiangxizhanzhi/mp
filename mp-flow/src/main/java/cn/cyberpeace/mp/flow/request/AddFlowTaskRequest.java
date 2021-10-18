package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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

}
