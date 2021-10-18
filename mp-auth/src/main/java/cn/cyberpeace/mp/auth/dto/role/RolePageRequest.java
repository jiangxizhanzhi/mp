package cn.cyberpeace.mp.auth.dto.role;

import cn.cyberpeace.mp.auth.dto.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RolePageRequest extends PageRequest {

    @ApiModelProperty(value = "角色名称")
    private String name;

    @Override
    public void ensure() {
        super.ensure();
        if (super.getOrders() == null || super.getOrders().length == 0) {
            super.setOrders(new String[]{"-update_time"});
        }
    }
}
