package cn.cyberpeace.mp.auth.bo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel
public class UserOnlineCntBo {

    @ApiModelProperty(value = "用户总数")
    private Integer totalUser;

    @ApiModelProperty(value = "在线用户数")
    private Integer onlineUser;

}
