package cn.cyberpeace.mp.flow.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "流量集合通用查询条件")
public class FlowCollectionQueryParams extends QueryParams {

    
    @ApiModelProperty(value = "流量分类")
    private Integer category;

}
