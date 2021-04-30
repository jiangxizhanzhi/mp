package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_user")
public class UserModel extends BaseModel {
    private String tenant;
    private String identifier;
    private String username;
    private String password;
    private String name;
    private String nickname;
    private String avatar;
    private String birthday;
    private String email;
    private String mobile;
    private String idCard;
    private String address;
    private String status;
    private Date dateJoined;
    private Date expiredTime;
    private Date lastLogin;
}
