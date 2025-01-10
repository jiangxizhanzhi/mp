package cn.cyberpeace.mp.flow.bo;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlowActuatorQueryParams extends QueryParams {
    @ApiModelProperty(value = "场景id")
    private String csceneInstanceId;

    @ApiModelProperty(value = "状态")
    private Integer status ;
}
