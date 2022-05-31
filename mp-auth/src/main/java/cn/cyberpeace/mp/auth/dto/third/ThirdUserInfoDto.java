package cn.cyberpeace.mp.auth.dto.third;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 第三方用户返回信息
 * @Author qiaokang
 * @Date：2022/5/31 13:52
 */
@ApiModel
@Data
public class ThirdUserInfoDto {

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "第三方token")
    private String thirdToken;

}
