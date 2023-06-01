package cn.cyberpeace.mp.sceneplugincollection.service.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TimeListPageInfoQueryParams extends ListPageInfoQueryParams {
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
