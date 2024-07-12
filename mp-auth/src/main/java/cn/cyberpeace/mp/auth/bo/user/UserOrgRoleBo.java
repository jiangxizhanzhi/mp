 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserOrgRoleBo.java.
 // File size 519.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.bo.user;

import cn.cyberpeace.mp.auth.model.UserModel;
import lombok.Data;

/**
 * @ClassName UserOrgRoleBo
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/7/22 09:43
 * @Version 1.0.0
 * @see UserModel
 */
@Data
public class UserOrgRoleBo extends UserModel {

    public static String FIELD_USER = "user";
    public static String FIELD_ORGS = "orgs";
    public static String FIELD_ROLES = "roles";

    private String orgs;

    private String roles;

}
