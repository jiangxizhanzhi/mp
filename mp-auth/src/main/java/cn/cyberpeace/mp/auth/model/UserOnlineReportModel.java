package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_user_online_report")
public class UserOnlineReportModel extends BaseModel {

    public static final Integer ONLINE = 1;

    public static final Integer OFFLINE = 0;

    @ApiModelProperty(value = "用户id")
    private String user;

    @ApiModelProperty(value = "租户")
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户姓名")
    private String name;

    @ApiModelProperty(value = "组织")
    private String org;

    @DefaultStr
    @ApiModelProperty(value = "动作")
    private String action;

    @DefaultStr
    @ApiModelProperty(value = "对象")
    private String subject;

    @ApiModelProperty(value = "累计在线时长（小时）")
    private Double totalOnlineHours;

    @DefaultStr
    @ApiModelProperty(value = "监控")
    private String monitor;

    @ApiModelProperty(value = "是否在线 0:不在线 1:在线")
    private Integer isOnline;
}
