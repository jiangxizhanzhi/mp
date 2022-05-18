package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.response.BatchActuatorCallbackResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.function.Consumer;

/**
 * @Description: 批量执行流量任务请求
 * @Author qiaokang
 * @Date：2021/10/11 16:16
 */
@Data
@ApiModel(value = "批量执行流量任务请求")
public class BatchActuatorTaskRequest {

    /**
     * 执行类型：1 start， 2 stop
     */
    @ApiModelProperty(value = "执行类型：1 start， 2 stop")
    @NotNull(message = "taskAction can not be empty")
    private Integer taskAction;

    /**
     * 任务Id集合
     */
    @ApiModelProperty(value = "任务Id集合")
    @NotNull(message = "taskIds can not be empty")
    private List<String> taskIds;

    /**
     * 流量停止回调方法
     */
    @ApiModelProperty(value = "流量停止回调方法")
    private Consumer<BatchActuatorCallbackResponse> callbackConsumer;

    /**
     * 执行方式：0 串行，1 并行
     */
    @ApiModelProperty(value = "执行方式：0 串行，1 并行")
    private int runType;

}
