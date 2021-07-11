package cn.cyberpeace.mp.flow.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "流量集合关联通用查询条件")
public class FlowCollectionRelQueryParams extends QueryParams {

    
    @ApiModelProperty(value = "流量集合id")
    private String collectionId;

}
