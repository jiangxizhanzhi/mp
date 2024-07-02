package cn.cyberpeace.mp.cscene.controller.request;

import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName CsceneInstancesActionRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class CsceneInstancesActionRequest {
    @ApiModelProperty(value = "id列表")
    private String[] ids;
    @ApiModelProperty(value = "场景上下文")
    private CsceneContextBO context;
}
