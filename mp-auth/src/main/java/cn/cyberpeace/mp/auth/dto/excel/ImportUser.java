package cn.cyberpeace.mp.auth.dto.excel;

import lombok.Data;

import java.util.Date;

@Data
public class ImportUser {

    // 用户名，不能重复
    private String username;

    // 用户姓名
    private String name;

    // 密码，通过Excel导入的用户，密码为明文，且忽略密码强度限制
    private String password;

    // 组织路径，多个用逗号隔开
    private String orgpath;

    // 角色 identifier，多个用逗号隔开
    private String roles;

}
