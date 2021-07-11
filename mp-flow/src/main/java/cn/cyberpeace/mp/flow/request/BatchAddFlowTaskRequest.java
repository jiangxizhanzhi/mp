package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel(value = "批量新增流量任务请求")
public class BatchAddFlowTaskRequest {

    
    @ApiModelProperty(value = "新增流量任务请求集合")
    private List<AddFlowTaskRequest> addTaskRequestList;

}
