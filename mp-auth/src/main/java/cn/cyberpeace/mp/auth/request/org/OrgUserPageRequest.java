 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OrgUserPageRequest.java.
 // File size 1822.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.request.org;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.request.common.PageRequest;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName OrgUserPageRequest
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see PageRequest
 */
@Data
public class OrgUserPageRequest extends PageRequest {

    // 注意 后台会匹配 username 或者 name
    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "组织id")
    private String org;

    @ApiModelProperty(value = "用户id列表")
    private String[] ids;

    @ApiModelProperty(value = "是否展示该组织以下所有层级的用户，默认为true")
    private boolean showAllUnderLevel = true;

    @ApiModelProperty(value = "1: 正常 0：禁用")
    private Integer isEnabled;

    // 1：已审核 0：待审核
    @ApiModelProperty(value = "1：已审核 0：待审核")
    private Integer isAudited;

    @ApiModelProperty(value = "1：已过期 0：未过期")
    private Integer isExpired;


    @Override
    public void ensure() {
        super.ensure();
        if (super.getOrders() == null || super.getOrders().length == 0) {
            String updateTime = BeanUtils.getBeanGetterFieldName(UserModel::getUpdateTime);
            String username = BeanUtils.getBeanGetterFieldName(UserModel::getUsername);
            updateTime = TextUtils.humpToLine(updateTime);
            username = TextUtils.humpToLine(username);

            super.setOrders(new String[]{"-" + updateTime, username});
        }
    }
}
