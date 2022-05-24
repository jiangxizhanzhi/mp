package cn.cyberpeace.mp.auth.dto.login;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 根据token获取用户信息返回体
 * @Author qiaokang
 * @Date：2021/8/31 11:13
 */
@ApiModel(value = "根据token获取用户信息返回体")
@Data
public class GetTokenResponse {

    @ApiModelProperty(value = "状态码")
    private Integer status;

    @ApiModelProperty(value = "信息")
    private String msg;

    private User data;

    @Data
    public static final class User {

        private String userId;

        private String userName;


    }

}
