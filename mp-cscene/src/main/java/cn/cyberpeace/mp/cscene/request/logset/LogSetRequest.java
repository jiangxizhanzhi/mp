package cn.cyberpeace.mp.cscene.request.logset;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description: 修改场景日志收集周期请求体
 */
@Data
@ApiModel("修改场景日志收集周期请求体")
public class LogSetRequest {

    /**
     * 场景id
     */
    @ApiModelProperty("场景id")
    @NotNull(message = "sceneId can not be empty")
    private String sceneId;

    /**
     * 周期
     */
    @ApiModelProperty("周期,以天为单位")
    @NotNull(message = "days can not be empty")
    private Integer days;
}
