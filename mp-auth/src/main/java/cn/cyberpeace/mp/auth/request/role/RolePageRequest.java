package cn.cyberpeace.mp.auth.request.role;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.request.common.PageRequest;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.hutool.core.util.StrUtil;
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
            String updateTime = BeanUtils.getBeanGetterFieldName(RoleModel::getUpdateTime);
            updateTime = StrUtil.toUnderlineCase(updateTime);
            super.setOrders(new String[]{"-" + updateTime});
        }
    }
}
