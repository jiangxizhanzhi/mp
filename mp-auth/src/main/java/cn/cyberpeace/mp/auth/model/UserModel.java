package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.generator.DateGenerator;
import cn.cyberpeace.mp.base.model.generator.DateTimeGenerator;
import cn.cyberpeace.mp.base.model.generator.UuidGenerator;
import cn.cyberpeace.mp.base.model.stereotype.DefaultGen;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_user")
public class UserModel extends BaseModel implements Serializable {

    public static final int INT_CHECK_NO = 0;

    public static final int INT_CHECK_YES = 1;


    @DefaultStr(AuthConstant.DEFAULT_TENANT)
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

    @ApiModelProperty(value = "头像")
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
    @DefaultInt(1)
    private Integer status;
    @DefaultInt(INT_CHECK_NO)
    private Integer initCheck;
    @DefaultStr
    private String description;
    @DefaultGen(DateTimeGenerator.class)
    private LocalDateTime dateJoined;
    private LocalDateTime expiredTime;
    @DefaultGen(DateTimeGenerator.class)
    private LocalDateTime lastLogin;

    public static class Status {
        // 正常
        public static final Integer NORMAL = 1;
        // 禁用状态
        public static final Integer DISABLE = 2;
        // 挂起，等待审批
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserModel userModel = (UserModel) o;
        return Objects.equals(tenant, userModel.tenant) && Objects.equals(identifier, userModel.identifier) && Objects.equals(username, userModel.username) && Objects.equals(password, userModel.password) && Objects.equals(tokenSecret, userModel.tokenSecret) && Objects.equals(name, userModel.name) && Objects.equals(nickname, userModel.nickname) && Objects.equals(avatar, userModel.avatar) && Objects.equals(birthday, userModel.birthday) && Objects.equals(email, userModel.email) && Objects.equals(mobile, userModel.mobile) && Objects.equals(idCard, userModel.idCard) && Objects.equals(address, userModel.address) && Objects.equals(status, userModel.status) && Objects.equals(initCheck, userModel.initCheck) && Objects.equals(description, userModel.description) && Objects.equals(dateJoined, userModel.dateJoined) && Objects.equals(expiredTime, userModel.expiredTime) && Objects.equals(lastLogin, userModel.lastLogin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tenant, identifier, username, password, tokenSecret, name, nickname, avatar, birthday, email, mobile, idCard, address, status, initCheck, description, dateJoined, expiredTime, lastLogin);
    }
}
