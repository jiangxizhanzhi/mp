package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_role")
public class RoleModel extends BaseModel {

    @ApiModelProperty(value = "租户")
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;

    @ApiModelProperty(value = "所属组织")
    @DefaultStr("default")
    private String org;

    @ApiModelProperty(value = "编号")
    private String identifier;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "描述")
    @DefaultStr
    private String description;


    @Builder
    public RoleModel(String id, LocalDateTime createTime, LocalDateTime updateTime, String tenant, String org, String identifier, String name, String description) {
        super(id, createTime, updateTime);
        this.tenant = tenant;
        this.org = org;
        this.identifier = identifier;
        this.name = name;
        this.description = description;
    }

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
