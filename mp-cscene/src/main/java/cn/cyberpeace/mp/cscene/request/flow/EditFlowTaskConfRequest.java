package cn.cyberpeace.mp.cscene.request.flow;

import cn.cyberpeace.mp.flow.bo.FlowTaskConf;
import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("修改流量任务配置请求")
public class EditFlowTaskConfRequest extends FlowTaskConfModel {

    
    @ApiModelProperty(value = "流量任务配置")
    private FlowTaskConf flowTaskConf;
}
