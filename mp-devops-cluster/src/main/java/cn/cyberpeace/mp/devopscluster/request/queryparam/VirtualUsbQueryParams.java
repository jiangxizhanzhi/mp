package cn.cyberpeace.mp.devopscluster.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("虚拟USB通用查询条件")
public class VirtualUsbQueryParams extends QueryParams {

    
    @ApiModelProperty(value = "名称")
    private String name;

}
