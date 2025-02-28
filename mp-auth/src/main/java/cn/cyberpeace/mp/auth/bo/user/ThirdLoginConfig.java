package cn.cyberpeace.mp.auth.bo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class ThirdLoginConfig {
    @ApiModelProperty(value = "第三方登录页的url")
    private String thirdLoginPageUrl;
}
