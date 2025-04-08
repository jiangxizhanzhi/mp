package cn.cyberpeace.mp.behavior.req;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SimulatePageReq extends QueryParams {

    @ApiModelProperty(value = "实验任务名称")
    private String name;

    @ApiModelProperty(value = "状态")
    private Integer status;
}
