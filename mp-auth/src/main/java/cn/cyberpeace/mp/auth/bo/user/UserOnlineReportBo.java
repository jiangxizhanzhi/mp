package cn.cyberpeace.mp.auth.bo.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class UserOnlineReportBo {

    @ApiModelProperty(value = "用户id")
    private String user;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户姓名")
    private String name;

    @ApiModelProperty(value = "组织")
    private String org;

    @ApiModelProperty(value = "动作")
    private String action;

    @ApiModelProperty(value = "对象")
    private String subject;

    @ApiModelProperty(value = "累计在线时长（小时）")
    private Double totalOnlineHours;

    @ApiModelProperty(value = "是否在线 0:不在线 1:在线")
    private Integer isOnline;

}
