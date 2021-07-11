package cn.cyberpeace.mp.cscene.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "截屏通用查询条件")
public class ScreenshotQueryParams extends QueryParams {

    
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    
    @ApiModelProperty(value = "结束时间")
    private String endTime;

}
