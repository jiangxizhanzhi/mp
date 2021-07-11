package cn.cyberpeace.mp.cscene.request.flow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel(value = "新增流量执行任务请求")
public class AddFlowTaskTaskRequest {

    
    @ApiModelProperty(value = "详情")
    private List<AddFlowTaskTaskDetail> details;

    
    @ApiModelProperty(value = "场景实例ID")
    private String id;
}
