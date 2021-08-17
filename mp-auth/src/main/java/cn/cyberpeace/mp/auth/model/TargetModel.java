package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.dto.validate.AddGroup;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_target")
@Builder
public class TargetModel extends BaseModel {

    public static Integer TYPE_MENU = 1;
    public static Integer TYPE_API = 2;
    public static Integer TYPE_DATA = 3;

    @ApiModelProperty(value = "上级权限")
    private String parent;

    @ApiModelProperty(value = "编号")
    @NotBlank(groups = AddGroup.class)
    private String identifier;

    @ApiModelProperty(value = "租户")
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;

    @ApiModelProperty(value = "名称")
    @NotBlank(groups = AddGroup.class)
    private String name;

    @ApiModelProperty(value = "路径")
    private String path;

    @ApiModelProperty(value = "类型 1：菜单 2：API")
    @NotNull(groups = AddGroup.class)
    private Integer type;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TargetModel that = (TargetModel) o;
        return Objects.equals(parent, that.parent) && Objects.equals(identifier, that.identifier) && Objects.equals(tenant, that.tenant) && Objects.equals(name, that.name) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parent, identifier, tenant, name, type);
    }
}
