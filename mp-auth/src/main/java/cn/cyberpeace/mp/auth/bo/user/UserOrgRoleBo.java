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

    public static String FIELD_USER = "user";
    public static String FIELD_ORGS = "orgs";
    public static String FIELD_ROLES = "roles";

    private String orgs;

    private String roles;

}
