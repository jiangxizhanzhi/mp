package cn.cyberpeace.mp.cscene.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "自动配置数据")
@Getter
@Setter
public class ScreenshotDeleteConfigResponse {

    @ApiModelProperty(value = "过期月数")
    private Integer months = 1;
    @ApiModelProperty(value = "是否开启")
    private Boolean enable = false;
    @ApiModelProperty(value = "定时任务执行时间")
    private String cron = "0 0 * * * ?";

}
