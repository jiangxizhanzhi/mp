package cn.cyberpeace.mp.cscene.request.flow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/26
 */
@Data
@ApiModel("流量任务启动请求参数")
public class CsceneFlowTaskStartRequest {
    @ApiModelProperty("流量仿真id")
    String flowTaskId;
    @ApiModelProperty("流量执行器id")
    String flowGeneratorId;
    @ApiModelProperty("目标机器id")
    String targetId;
}
