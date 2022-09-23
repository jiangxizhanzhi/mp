package cn.cyberpeace.mp.auth.dto.user;

import cn.cyberpeace.mp.auth.model.UserModel;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/22 09:43
 */
@Data
public class UserOrgRoleDto extends UserModel {

    public static String USER = "user";
    public static String ORGS = "orgs";
    public static String ROLES = "roles";

    private String orgs;

    private String roles;

}
