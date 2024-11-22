package cn.cyberpeace.mp.auth.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThirdSyncUserType {
    private String typeName;  // 用户类型名称
    private String typeCode;  // 用户类型code
}