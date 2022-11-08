package cn.cyberpeace.mp.cscene.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description: 按时间删除截屏请求
 * @Author qiaokang
 * @Date：2021/9/14 10:50
 */
@Data
@ApiModel("按时间删除截屏请求")
public class ScreenshotDeleteRequest {

    /**
     * 删除条件：1: 6个月之前，2：3个月之前
     */
    @ApiModelProperty("删除条件：1: 6个月之前，2：3个月之前")
    @NotNull(message = "recordTime can not be empty")
    private Integer recordTime;

}
