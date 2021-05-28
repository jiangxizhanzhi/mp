package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.generator.DateGenerator;
import cn.cyberpeace.mp.base.model.generator.DateTimeGenerator;
import cn.cyberpeace.mp.base.model.generator.UuidGenerator;
import cn.cyberpeace.mp.base.model.stereotype.DefaultGen;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_user")
public class UserModel extends BaseModel implements Serializable {
    @DefaultStr
    private String tenant;
    private String identifier;
    private String username;
    private String password;
    @DefaultGen(UuidGenerator.class)
    private String tokenSecret;
    @DefaultStr
    private String name;
    @DefaultStr
    private String nickname;
    @DefaultStr
    private String avatar;
    @DefaultGen(DateGenerator.class)
    private LocalDate birthday;
    @DefaultStr
    private String email;
    @DefaultStr
    private String mobile;
    @DefaultStr
    private String idCard;
    @DefaultStr
    private String address;
    @DefaultInt(3)
    private Integer status;
    @DefaultGen(DateTimeGenerator.class)
    private LocalDateTime dateJoined;
    private LocalDateTime expiredTime;
    @DefaultGen(DateTimeGenerator.class)
    private LocalDateTime lastLogin;

    public static class Status {
        public static final Integer NORMAL = 1;
        public static final Integer DISABLE = 2;
        public static final Integer PENDING = 3;

        private Status() {}
    }

    public void encodePassword() {
        if (!StringUtils.isEmpty(password)) {
            password = PasswordUtils.encode(password);
        }
    }

    public void initDefaultIdentifier() {
        if (StringUtils.isEmpty(identifier) && !StringUtils.isEmpty(username)) {
            identifier = username;
        }
    }

    public void initDefaultExpiredTime() {
        if (expiredTime == null) {
            expiredTime =  LocalDateTime.now().plusYears(99L);
        }
    }
}
