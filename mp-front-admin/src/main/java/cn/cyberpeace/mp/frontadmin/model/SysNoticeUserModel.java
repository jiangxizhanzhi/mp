 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysNoticeUserModel.java.
 // File size 895.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 公告-用户表
 * @Author zhouxinghuai
 * @Date：2022/9/20 11:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_sys_notice_user")
public class SysNoticeUserModel extends BaseModel {

    @ApiModelProperty(value = "租户")
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;

    @ApiModelProperty(value = "公告ID")
    private String noticeId;

    @ApiModelProperty(value = "用户ID")
    private String user;
}
