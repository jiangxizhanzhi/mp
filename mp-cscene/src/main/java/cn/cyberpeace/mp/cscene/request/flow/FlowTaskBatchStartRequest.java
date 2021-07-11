package cn.cyberpeace.mp.cscene.request.flow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("流量任务批量启动请求")
public class FlowTaskBatchStartRequest {

    @ApiModelProperty("任务id集合")
    private List<String> taskIds;

}
