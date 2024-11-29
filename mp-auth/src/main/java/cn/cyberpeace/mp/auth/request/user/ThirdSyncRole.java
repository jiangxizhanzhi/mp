package cn.cyberpeace.mp.auth.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThirdSyncRole {
    private String role_Name;  // 用户类型名称
    private String role_code;  // 用户类型code
}