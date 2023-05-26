package cn.cyberpeace.mp.cscene.request.flow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: 流量任务批量启动请求
 * @Author: guquan
 * @Date：2023/5/26 10:48
 */
@Data
@ApiModel("流量任务批量启动请求")
public class FlowTaskBatchStartRequest {

    @ApiModelProperty("任务id集合")
    private List<String> taskIds;

}
