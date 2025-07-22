package cn.cyberpeace.mp.auth.bo.redis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddUserMessage {

    private String id;

    private String username;

    private String password;

    private String displayName;

    private String departmentId;

    private String department;

    private Integer status;

    private String email;

    private String mobile;

    private Integer sortIndex;
}
