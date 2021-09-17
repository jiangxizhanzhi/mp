package cn.cyberpeace.mp.devops.cluster.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 截屏通用查询条件
 * @Author qiaokang
 * @Date：2021/8/31 17:17
 */
@Data
@ApiModel(value = "截屏通用查询条件")
public class ScreenshotQueryParams extends QueryParams {

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private String endTime;

}
