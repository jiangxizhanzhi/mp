package cn.cyberpeace.mp.cscene.controller.request;

import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CsceneInstanceNodeProbesActionRequest extends CsceneInstanceNodeActionRequest {

    @ApiModelProperty(value = "探针集合")
    private List<Probe> probeList;

}
