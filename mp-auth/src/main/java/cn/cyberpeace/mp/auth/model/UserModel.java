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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@ApiModel("用户信息")
public class UserModel extends BaseModel implements Serializable {

    public static final int INT_CHECK_NO = 0;

    public static final int INT_CHECK_YES = 1;


    @ApiModelProperty(value = "主键")
    @DefaultInt(0)
    private Integer pk;

    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    @ApiModelProperty(value = "租户")
    private String tenant;

    @ApiModelProperty(value = "编号")
    private String identifier;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    // 该字段对读屏蔽，对写不屏蔽
    private String password;

    @ApiModelProperty(value = "token秘钥")
    @DefaultGen(UuidGenerator.class)
    // 该字段对读屏蔽，对写不屏蔽
    private String tokenSecret;

    @ApiModelProperty(value = "用户姓名")
    @DefaultStr
    private String name;

    @ApiModelProperty(value = "昵称")
    @DefaultStr
    private String nickname;

    @ApiModelProperty(value = "头像")
    @DefaultStr
    private String avatar;

    @ApiModelProperty(value = "生日")
    @DefaultGen(DateGenerator.class)
    private LocalDate birthday;

    @ApiModelProperty(value = "邮箱")
    @DefaultStr
    private String email;

    @ApiModelProperty(value = "手机号")
    @DefaultStr
    private String mobile;

    @ApiModelProperty(value = "身份证号")
    @DefaultStr
    private String idCard;

    @ApiModelProperty(value = "地址")
    @DefaultStr
    private String address;


    @ApiModelProperty(value = "是否首次登陆需要强制修改密码 0:否，1:是")
    @DefaultInt(INT_CHECK_NO)
    private Integer initCheck;

    @ApiModelProperty(value = "描述")
    @DefaultStr
    private String description;

    // 1：正常， 0：禁用
    @ApiModelProperty(value = "1: 正常 0：禁用")
    @DefaultInt(1)
    private Integer isEnabled;

    // 1：已审核 0：待审核
    @ApiModelProperty(value = "1：已审核 0：待审核")
    @DefaultInt(1)
    private Integer isAudited;


    @ApiModelProperty(value = "加入时间")
    @DefaultGen(DateTimeGenerator.class)
    private LocalDateTime dateJoined;

    @ApiModelProperty(value = "过期时间")
    private LocalDateTime expiredTime;

    @ApiModelProperty(value = "最后登录时间")
    @DefaultGen(DateTimeGenerator.class)
    private LocalDateTime lastLogin;


    @Builder
    public UserModel(String id, LocalDateTime createTime, LocalDateTime updateTime, String tenant, String identifier, String username, String password, String tokenSecret, String name, String nickname, String avatar, LocalDate birthday, String email, String mobile, String idCard, String address, Integer initCheck, String description, Integer isEnabled, Integer isAudited, LocalDateTime dateJoined, LocalDateTime expiredTime, LocalDateTime lastLogin) {
        super(id, createTime, updateTime);
        this.tenant = tenant;
        this.identifier = identifier;
        this.username = username;
        this.password = password;
        this.tokenSecret = tokenSecret;
        this.name = name;
        this.nickname = nickname;
        this.avatar = avatar;
        this.birthday = birthday;
        this.email = email;
        this.mobile = mobile;
        this.idCard = idCard;
        this.address = address;
        this.initCheck = initCheck;
        this.description = description;
        this.isEnabled = isEnabled;
        this.isAudited = isAudited;
        this.dateJoined = dateJoined;
        this.expiredTime = expiredTime;
        this.lastLogin = lastLogin;
    }

    public static class Status {
        // 启用与否
        public static final Integer ENABLED = 1;
        public static final Integer DISABLED = 0;

        // 审核与否
        public static final Integer AUDITED = 1;
        public static final Integer NOT_AUDITED = 0;

        // 过期与否
        public static final Integer EXPIRED = 1;
        public static final Integer NOT_EXPIRED = 0;

        private Status() {
        }
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
            expiredTime = LocalDateTime.now().plusYears(99L);
        }
    }

    @JsonIgnore
    public String getTokenSecret() {
        return tokenSecret;
    }

    @JsonProperty
    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserModel userModel = (UserModel) o;
        return Objects.equals(tenant, userModel.tenant) && Objects.equals(identifier, userModel.identifier) && Objects.equals(username, userModel.username) && Objects.equals(password, userModel.password) && Objects.equals(tokenSecret, userModel.tokenSecret) && Objects.equals(name, userModel.name) && Objects.equals(nickname, userModel.nickname) && Objects.equals(avatar, userModel.avatar) && Objects.equals(birthday, userModel.birthday) && Objects.equals(email, userModel.email) && Objects.equals(mobile, userModel.mobile) && Objects.equals(idCard, userModel.idCard) && Objects.equals(address, userModel.address) && Objects.equals(initCheck, userModel.initCheck) && Objects.equals(description, userModel.description) && Objects.equals(isEnabled, userModel.isEnabled) && Objects.equals(isAudited, userModel.isAudited) && Objects.equals(dateJoined, userModel.dateJoined) && Objects.equals(expiredTime, userModel.expiredTime) && Objects.equals(lastLogin, userModel.lastLogin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tenant, identifier, username, password, tokenSecret, name, nickname, avatar, birthday, email, mobile, idCard, address, initCheck, description, isEnabled, isAudited, dateJoined, expiredTime, lastLogin);
    }
}
