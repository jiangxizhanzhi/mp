package cn.cyberpeace.mp.flow.request.out;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 修改流量任务配置请求
 * @Author: guquan
 * @Date：2023/5/9 19:01
 */
@Data
public class OutputStartTaskRequest {
    @ApiModelProperty(value = "任务id")
    private String taskId;
}
