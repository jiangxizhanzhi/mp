package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName CsceneInstanceNodeFlowRecordsRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CsceneInstanceNodeActionRequest
 */
@Data
public class CsceneInstanceNodeFlowRecordsRequest extends CsceneInstanceNodeActionRequest {
    @ApiModelProperty(value = "流量id")
    private String flowId;
    @ApiModelProperty(value = "是否批量查询")
    private Boolean isBatch;
}
