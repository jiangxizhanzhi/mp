package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CsceneInstanceNodeProbesActionRequest extends CsceneInstanceNodeActionRequest {
    @ApiModelProperty(value = "探针ids")
    private List<String> probeIds;
}
