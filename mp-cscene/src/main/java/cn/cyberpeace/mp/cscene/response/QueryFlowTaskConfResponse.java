package cn.cyberpeace.mp.cscene.response;

import cn.cyberpeace.mp.flow.bo.FlowTaskConf;
import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("查询流量任务配置响应体")
public class QueryFlowTaskConfResponse extends FlowTaskConfModel {

    
    @ApiModelProperty(value = "流量任务配置")
    private FlowTaskConf flowTaskConf;
}
