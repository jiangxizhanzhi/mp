package cn.cyberpeace.mp.flow.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value = "创建流量执行器返回信息")
@Getter
@Setter
public class StartFlowActuatorResponse {
    @ApiModelProperty("流量执行器id")
    private String id;
}
