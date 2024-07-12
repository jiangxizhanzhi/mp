 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RolePageRequest.java.
 // File size 981.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.request.role;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.request.common.PageRequest;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName RolePageRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see PageRequest
 */
@Data
public class RolePageRequest extends PageRequest {

    @ApiModelProperty(value = "角色名称")
    private String name;

    @Override
    public void ensure() {
        super.ensure();
        if (super.getOrders() == null || super.getOrders().length == 0) {
            String updateTime = BeanUtils.getBeanGetterFieldName(RoleModel::getUpdateTime);
            updateTime = TextUtils.humpToLine(updateTime);
            super.setOrders(new String[]{"-" + updateTime});
        }
    }
}
