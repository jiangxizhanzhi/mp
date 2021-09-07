package cn.cyberpeace.mp.flow.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 流量集合关联通用查询条件
 * @Author qiaokang
 * @Date：2021/8/31 17:17
 */
@Data
@ApiModel(value = "流量集合关联通用查询条件")
public class FlowCollectionRelQueryParams extends QueryParams {

    /**
     * 流量集合id
     */
    @ApiModelProperty(value = "流量集合id")
    private String collectionId;

}
