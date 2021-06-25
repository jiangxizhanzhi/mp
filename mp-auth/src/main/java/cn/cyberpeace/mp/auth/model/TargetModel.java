package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.dto.validate.AddGroup;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
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

    private String parent;
    @NotBlank(groups = AddGroup.class)
    private String identifier;
    private String tenant;
    @NotBlank(groups = AddGroup.class)
    private String name;
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
