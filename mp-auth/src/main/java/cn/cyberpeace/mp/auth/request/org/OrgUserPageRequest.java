package cn.cyberpeace.mp.auth.request.org;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.request.common.PageRequest;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrgUserPageRequest extends PageRequest {

    
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
            updateTime = StrUtil.toUnderlineCase(updateTime);
            username = StrUtil.toUnderlineCase(username);

            super.setOrders(new String[]{"-" + updateTime, username});
        }
    }
}
