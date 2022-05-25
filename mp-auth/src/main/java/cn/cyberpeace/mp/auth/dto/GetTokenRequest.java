package cn.cyberpeace.mp.auth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description: 获取token请求
 * @Author qiaokang
 * @Date：2022/5/24 15:43
 */
@ApiModel(value = "获取token请求")
@Data
public class GetTokenRequest {

    @ApiModelProperty(value = "用户id", required = true)
    @NotBlank(message = "userId can not be empty")
    private String userId;

}
