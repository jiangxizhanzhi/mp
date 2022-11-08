package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CsceneInstanceNodesActionRequest extends CsceneInstanceActionRequest {
    @ApiModelProperty(value = "节点id列表")
    private List<String> nodeIds;
}
