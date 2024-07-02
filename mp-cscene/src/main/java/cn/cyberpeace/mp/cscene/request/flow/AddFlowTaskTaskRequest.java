package cn.cyberpeace.mp.cscene.request.flow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName AddFlowTaskTaskRequest
 * @Description 新增流量执行任务请求
 * @Author guquan
 * @Date 2023/5/8 10:04
 * @Version 1.0.0
 */
@Data
@ApiModel(value = "新增流量执行任务请求")
public class AddFlowTaskTaskRequest {

    /**
     * 详情
     */
    @ApiModelProperty(value = "详情")
    private List<AddFlowTaskTaskDetail> details;

    /**
     * 场景实例ID
     */
    @ApiModelProperty(value = "场景实例ID")
    private String id;
}
