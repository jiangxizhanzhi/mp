package cn.cyberpeace.mp.auth.dto.role;

import cn.cyberpeace.mp.auth.model.RoleModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RoleRequest extends RoleModel {

    @ApiModelProperty(value = "权限id集合")
    List<String> targetIds;

}
