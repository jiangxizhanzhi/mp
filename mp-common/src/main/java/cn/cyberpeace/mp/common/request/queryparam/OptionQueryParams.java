package cn.cyberpeace.mp.common.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "选项通用查询条件")
public class OptionQueryParams extends QueryParams {

    
    @ApiModelProperty(value = "类型")
    private String type;

    
    @ApiModelProperty(value = "名称")
    private String zhName;

}
