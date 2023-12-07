package cn.cyberpeace.mp.cscene.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "自动配置数据")
@Getter
@Setter
public class ScreenshotDeleteConfigResponse {

    @ApiModelProperty(value = "过期月数")
    private Integer months;

}
