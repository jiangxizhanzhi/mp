package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_user_role")
public class UserRoleModel extends BaseModel {
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;
    private String user;
    private String role;

    @Builder
    public UserRoleModel(String id, LocalDateTime createTime, LocalDateTime updateTime, String tenant, String user, String role) {
        super(id, createTime, updateTime);
        this.tenant = tenant;
        this.user = user;
        this.role = role;
    }
}
