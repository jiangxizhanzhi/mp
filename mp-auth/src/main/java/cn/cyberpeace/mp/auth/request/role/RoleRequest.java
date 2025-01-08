package cn.cyberpeace.mp.auth.request.role;

import cn.cyberpeace.mp.auth.model.RoleModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data

public class RoleRequest extends RoleModel {

    @ApiModelProperty(value = "权限id集合")
    List<String> targetIds;
    /**
     * 关联靶场名称
     */
    private String bcName;
    /**
     * 关联靶场id
     */
    private String bcIds;

}
