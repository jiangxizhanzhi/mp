 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserRoleModel.java.
 // File size 1023.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
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
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
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
