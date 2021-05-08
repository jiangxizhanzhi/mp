package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.base.util.TextUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_user")
public class UserModel extends BaseModel implements Serializable {
    private String tenant;
    private String identifier;
    private String username;
    private String password;
    private String tokenSecret;
    private String name;
    private String nickname;
    private String avatar;
    private Date birthday;
    private String email;
    private String mobile;
    private String idCard;
    private String address;
    private Integer status;
    private Date dateJoined;
    private Date expiredTime;
    private Date lastLogin;

    public enum Status {
        NORMAL(1),
        DISABLE(2),
        PENDING(3);

        private final Integer value;
        Status(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    public void encodePassword() {
        if (!StringUtils.isEmpty(password)) {
            password = PasswordUtils.encode(password);
        }
    }

    public void initDefaultTenant() {
        if (StringUtils.isEmpty(tenant)) {
            tenant = StringUtils.EMPTY;
        }
    }

    public void initDefaultIdentifier() {
        if (StringUtils.isEmpty(identifier) && !StringUtils.isEmpty(username)) {
            identifier = username;
        }
    }

    public void initDefaultTokenSecret() {
        if (StringUtils.isEmpty(tokenSecret)) {
            tokenSecret = TextUtils.uuid();
        }
    }

    public void initDefaultName() {
        if (StringUtils.isEmpty(name)) {
            name = StringUtils.EMPTY;
        }
    }

    public void initDefaultNickname() {
        if (StringUtils.isEmpty(nickname)) {
            nickname = StringUtils.EMPTY;
        }
    }

    public void initDefaultAvatar() {
        if (StringUtils.isEmpty(avatar)) {
            avatar = StringUtils.EMPTY;
        }
    }

    public void initDefaultBirthday() {
        if (birthday == null) {
            birthday = new Date();
        }
    }

    public void initDefaultEmail() {
        if (StringUtils.isEmpty(email)) {
            email = StringUtils.EMPTY;
        }
    }

    public void initDefaultMobile() {
        if (StringUtils.isEmpty(mobile)) {
            mobile = StringUtils.EMPTY;
        }
    }

    public void initDefaultIdCard() {
        if (StringUtils.isEmpty(idCard)) {
            idCard = StringUtils.EMPTY;
        }
    }

    public void initDefaultAddress() {
        if (StringUtils.isEmpty(address)) {
            address = StringUtils.EMPTY;
        }
    }

    public void initDefaultStatus() {
        if (status == null) {
            status = Status.PENDING.getValue();
        }
    }

    public void initDefaultDateJoined() {
        if (dateJoined == null) {
            dateJoined = new Date();
        }
    }

    public void initDefaultExpiredTime() {
        if (expiredTime == null) {
            expiredTime = new Date(new Date().getTime() + 99L * 365 * 24 * 3600 * 1000);
        }
    }

    public void initDefaultLastLogin() {
        if (lastLogin == null) {
            lastLogin = new Date();
        }
    }
}
