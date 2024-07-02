package cn.cyberpeace.mp.frontadmin.request.notice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName DelayNoticeRequest
 * @Description 延迟提醒请求
 * @Author qiaokang
 * @Date 2023/8/22 16:36
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class DelayNoticeRequest {

    @ApiModelProperty(value = "延迟提醒方式：0 7天后提醒， 1 不再提醒")
    private int delayType;

}
