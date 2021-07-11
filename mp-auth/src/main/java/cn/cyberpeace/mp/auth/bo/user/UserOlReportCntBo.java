package cn.cyberpeace.mp.auth.bo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel
public class UserOlReportCntBo {

    @ApiModelProperty("用户id")
    private String uid;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("用户姓名")
    private String uname;

    @ApiModelProperty("在线用户数")
    private Integer cnt;

}
