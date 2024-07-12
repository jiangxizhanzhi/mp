 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserOnlineReportBo.java.
 // File size 932.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.bo.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName UserOnlineReportBo
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/10/26 10:32
 * @Version 1.0.0
 */
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
