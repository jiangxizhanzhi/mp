package cn.cyberpeace.mp.flow.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 流量发生器通用查询条件
 */
@Data
@ApiModel(value = "流量发生器通用查询条件")
public class FlowActuatorQueryParams extends QueryParams {

    /**
     * 场景实例id
     */
    @ApiModelProperty(value = "场景实例id")
    private String instanceId;

    /**
     * 状态
     * */
    @ApiModelProperty(value = "流量运行状态：0 初始化， 1 运行中， 2 停止中， 3 已停止， 4 未启动过")
    private Integer status;
}
