package cn.cyberpeace.mp.auth.bo.role;

import cn.cyberpeace.mp.auth.bo.target.PermissionBo;
import cn.cyberpeace.mp.auth.model.RoleModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RolePermBo extends RoleModel {

    @ApiModelProperty(value = "角色绑定的用户数量")
    private Integer userCnt;

    @ApiModelProperty(value = "角色绑定的permssiondto列表 为保持兼容，命名为targets")
    private List<PermissionBo> targets;
}
