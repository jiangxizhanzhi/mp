 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserRoleModel.java.
 // File size 1087.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @ClassName UserRoleModel
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_user_role")
public class UserRoleModel extends BaseModel {
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;
    private String user;
    private String role;

    @Builder
    public UserRoleModel(String id, LocalDateTime createTime, LocalDateTime updateTime, String tenant, String user, String role) {
        super(id, createTime, updateTime);
        this.tenant = tenant;
        this.user = user;
        this.role = role;
    }
}
