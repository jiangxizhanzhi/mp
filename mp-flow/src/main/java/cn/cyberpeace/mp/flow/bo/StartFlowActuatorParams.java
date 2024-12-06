package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartFlowActuatorParams {
    @ApiModelProperty("场景模板id")
    private String csceneId;
    @ApiModelProperty("场景实例id")
    private String csceneInstanceId;
    @ApiModelProperty("执行器名称")
    private String actuatorName;
}
