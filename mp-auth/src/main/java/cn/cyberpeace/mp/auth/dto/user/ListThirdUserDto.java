package cn.cyberpeace.mp.auth.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 批量获取第三方用户
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
@ApiModel
public class ListThirdUserDto {

    @ApiModelProperty(value = "第三方用户id")
    private String thirdUserId;

    @ApiModelProperty(value = "第三方用户名称")
    private String thirdUserName;

}
