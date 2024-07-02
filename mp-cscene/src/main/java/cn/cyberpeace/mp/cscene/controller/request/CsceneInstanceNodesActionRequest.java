package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName CsceneInstanceNodesActionRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CsceneInstanceActionRequest
 */
@Data
public class CsceneInstanceNodesActionRequest extends CsceneInstanceActionRequest {
    @ApiModelProperty(value = "节点id列表")
    private List<String> nodeIds;
}
