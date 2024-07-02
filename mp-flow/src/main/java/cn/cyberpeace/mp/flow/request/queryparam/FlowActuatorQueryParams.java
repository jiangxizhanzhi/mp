package cn.cyberpeace.mp.flow.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FlowActuatorQueryParams
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see QueryParams
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

    /**
     *  分页计算偏差值
     */
    private Integer pageFrontIndex;
}
