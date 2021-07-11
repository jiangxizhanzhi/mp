package cn.cyberpeace.mp.auth.bo.user;

import cn.cyberpeace.mp.auth.model.UserModel;
import lombok.Data;


@Data
public class UserOrgRoleBo extends UserModel {

    public static String FIELD_USER = "user";
    public static String FIELD_ORGS = "orgs";
    public static String FIELD_ROLES = "roles";

    private String orgs;

    private String roles;

}
