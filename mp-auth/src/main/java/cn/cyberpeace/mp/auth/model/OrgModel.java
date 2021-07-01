package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_org")
public class OrgModel extends BaseModel {

    @ApiModelProperty(value = "租户")
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;

    @ApiModelProperty(value = "上级组织ID")
    @DefaultStr
    private String parent;

    @ApiModelProperty(value = "编号")
    @DefaultStr
    private String identifier;

    @ApiModelProperty(value = "名称")
    @DefaultStr
    private String name;

    @ApiModelProperty(value = "路径")
    @DefaultStr
    private String path;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OrgModel orgModel = (OrgModel) o;
        return Objects.equals(tenant, orgModel.tenant) && Objects.equals(parent, orgModel.parent) && Objects.equals(identifier, orgModel.identifier) && Objects.equals(name, orgModel.name) && Objects.equals(path, orgModel.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tenant, parent, identifier, name, path);
    }
}
