 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserOrgRoleBo.java.
 // File size 455.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.bo.user;

import cn.cyberpeace.mp.auth.model.UserModel;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/22 09:43
 */
@Data
public class UserOrgRoleBo extends UserModel {

    public static final String FIELD_USER = "user";
    public static final String FIELD_ORGS = "orgs";
    public static final String FIELD_ROLES = "roles";

    private String orgs;

    private String roles;

}
