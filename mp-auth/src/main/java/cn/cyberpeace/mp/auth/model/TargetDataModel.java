package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.request.validate.AddGroup;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Objects;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_target_data")
@Builder
public class TargetDataModel extends BaseModel {

    @ApiModelProperty(value = "编号")
    @NotBlank(groups = AddGroup.class)
    private String identifier;

    @ApiModelProperty(value = "租户")
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;

    @ApiModelProperty(value = "名称")
    @NotBlank(groups = AddGroup.class)
    private String name;

    @ApiModelProperty(value = "说明")
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TargetDataModel that = (TargetDataModel) o;
        return Objects.equals(identifier, that.identifier) && Objects.equals(tenant, that.tenant) && Objects.equals(name, that.name) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), identifier, tenant, name, description);
    }
}
