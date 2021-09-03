package cn.cyberpeace.mp.auth.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
@ApiModel
public class UserOnlineCntDto {

    @ApiModelProperty(value = "用户总数")
    private Integer totalUser;

    @ApiModelProperty(value = "在线用户数")
    private Integer onlineUser;

}
