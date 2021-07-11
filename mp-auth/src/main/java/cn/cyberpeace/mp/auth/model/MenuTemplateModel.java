package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.request.validate.AddGroup;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Objects;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_menu_template_info")
public class MenuTemplateModel extends TargetModel {

    @ApiModelProperty(value = "模板id")
    private String templateId;

    @ApiModelProperty(value = "菜单id")
    private String menuId;

    @ApiModelProperty(value = "英文名称")
    @NotBlank(groups = AddGroup.class)
    private String enName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MenuTemplateModel that = (MenuTemplateModel) o;
        return Objects.equals(getParent(), that.getParent()) && Objects.equals(getIdentifier(), that.getIdentifier()) && Objects.equals(getTenant(), that.getTenant()) && Objects.equals(getName(), that.getName()) && Objects.equals(getPath(), that.getPath()) && Objects.equals(getType(), that.getType()) && Objects.equals(getVisible(), that.getVisible());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getParent(), getIdentifier(), getTenant(), getName(), getPath(), getType(), getVisible());
    }
}
