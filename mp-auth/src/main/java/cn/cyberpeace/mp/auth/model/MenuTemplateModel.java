package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.request.validate.AddGroup;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
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

/**
 * 这个target以后就当做menu
 *
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_menu_template_info")
@Builder
public class MenuTemplateModel extends BaseModel {

    @ApiModelProperty(value = "模板id")
    private String templateId;

    @ApiModelProperty(value = "上级权限")
    private String parent;

    @ApiModelProperty(value = "编号")
    @NotBlank(groups = AddGroup.class)
    private String identifier;

    @ApiModelProperty(value = "菜单id")
    private String menuId;

    @ApiModelProperty(value = "租户")
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;

    @ApiModelProperty(value = "名称")
    @NotBlank(groups = AddGroup.class)
    private String name;

    @ApiModelProperty(value = "英文名称")
    @NotBlank(groups = AddGroup.class)
    private String enName;

    @ApiModelProperty(value = "路径")
    private String path;

    @ApiModelProperty(value = "类型 1：菜单 2：API")
    @NotNull(groups = AddGroup.class)
    private Integer type;

    @ApiModelProperty(value = "可见性 1：可见 0：不可见")
    @DefaultInt(1)
    private Integer visible;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MenuTemplateModel that = (MenuTemplateModel) o;
        return Objects.equals(parent, that.parent) && Objects.equals(identifier, that.identifier) && Objects.equals(tenant, that.tenant) && Objects.equals(name, that.name) && Objects.equals(path, that.path) && Objects.equals(type, that.type) && Objects.equals(visible, that.visible);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parent, identifier, tenant, name, path, type, visible);
    }
}
