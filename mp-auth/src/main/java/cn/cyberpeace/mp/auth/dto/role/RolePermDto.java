package cn.cyberpeace.mp.auth.dto.role;

import cn.cyberpeace.mp.auth.dto.target.PermissionDto;
import cn.cyberpeace.mp.auth.model.RoleModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RolePermDto extends RoleModel {

    @ApiModelProperty(value = "角色绑定的用户数量")
    private Integer userCnt;

    @ApiModelProperty(value = "角色绑定的permssiondto列表 为保持兼容，命名为targets")
    private List<PermissionDto> targets;
}
