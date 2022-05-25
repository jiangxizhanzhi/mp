package cn.cyberpeace.mp.auth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 获取token请求
 * @Author qiaokang
 * @Date：2022/5/24 15:43
 */
@ApiModel
@Data
public class UserTokenRequest {

    @ApiModelProperty(value = "第三方token", required = true)
    private String token;

}
