package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
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
    private String id_card;
    private String address;
    private String status;
    private Date date_joined;
    private Date expired_time;
    private Date last_login;
}
