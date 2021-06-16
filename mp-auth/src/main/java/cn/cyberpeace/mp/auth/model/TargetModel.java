package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.dto.validate.AddGroup;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_target")
public class TargetModel extends BaseModel {
    @NotBlank(groups = AddGroup.class)
    private String identifier;
    @NotBlank(groups = AddGroup.class)
    private String name;
    @NotNull(groups = AddGroup.class)
    private Integer type;
}
