package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName BatchAddFlowTaskRequest
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@ApiModel(value = "批量新增流量任务请求")
public class BatchAddFlowTaskRequest {

    /**
     * 新增流量任务请求集合
     */
    @ApiModelProperty(value = "新增流量任务请求集合")
    private List<AddFlowTaskRequest> addTaskRequestList;

}
