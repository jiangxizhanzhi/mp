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
@ApiModel("流量任务操作请求参数")
public class CsceneFlowTaskActionRequest {
    @ApiModelProperty("流量任务记录id")
    private String recordId;
}
