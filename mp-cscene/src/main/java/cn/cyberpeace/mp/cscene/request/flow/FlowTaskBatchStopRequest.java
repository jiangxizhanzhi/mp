package cn.cyberpeace.mp.cscene.request.flow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName FlowTaskBatchStopRequest
 * @Description 流量任务批量停止请求
 * @Author guquan
 * @Date 2023/6/2 8:59
 * @Version 1.0.0
 */
@Data
@ApiModel("流量任务批量停止请求")
public class FlowTaskBatchStopRequest {

    @ApiModelProperty("任务id集合")
    private List<String> taskIds;
}
