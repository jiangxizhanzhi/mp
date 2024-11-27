package cn.cyberpeace.mp.auth.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThirdSyncRole {
    private String roleName;  // 用户类型名称
    private String roleCode;  // 用户类型code
}