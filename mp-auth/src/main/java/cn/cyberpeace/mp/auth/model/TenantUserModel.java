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
import java.util.Objects;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_tenant_user")
public class TenantUserModel extends BaseModel {
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;
    private String org;
    private String user;

    @Builder
    public TenantUserModel(String id, LocalDateTime createTime, LocalDateTime updateTime, String tenant, String org, String user) {
        super(id, createTime, updateTime);
        this.tenant = tenant;
        this.org = org;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TenantUserModel that = (TenantUserModel) o;
        return Objects.equals(tenant, that.tenant) && Objects.equals(org, that.org) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tenant, org, user);
    }
}
