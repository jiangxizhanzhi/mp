package cn.cyberpeace.mp.cscene.request.flow;

import cn.cyberpeace.mp.flow.bo.FlowTaskConf;
import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 修改流量任务配置请求
 * @Author: guquan
 * @Date：2023/5/9 19:01
 */
@Data
@ApiModel("修改流量任务配置请求")
public class EditFlowTaskConfRequest extends FlowTaskConfModel {

    /**
     * 流量任务配置
     */
    @ApiModelProperty(value = "流量任务配置")
    private FlowTaskConf flowTaskConf;
}
