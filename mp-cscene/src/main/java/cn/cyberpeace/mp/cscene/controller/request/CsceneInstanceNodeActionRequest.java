package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName CsceneInstanceNodeActionRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CsceneInstanceActionRequest
 */
@Data
public class CsceneInstanceNodeActionRequest extends CsceneInstanceActionRequest {
    @ApiModelProperty(value = "节点id")
    private String nodeId;
    @ApiModelProperty(value = "指定镜像")
    private String image;
}
