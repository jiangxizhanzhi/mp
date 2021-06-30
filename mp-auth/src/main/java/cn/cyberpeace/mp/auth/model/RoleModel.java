package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_role")
public class RoleModel extends BaseModel {
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;
    private String org;
    private String identifier;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RoleModel roleModel = (RoleModel) o;
        return Objects.equals(tenant, roleModel.tenant) && Objects.equals(org, roleModel.org) && Objects.equals(identifier, roleModel.identifier) && Objects.equals(name, roleModel.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tenant, org, identifier, name);
    }
}
