package cn.cyberpeace.mp.auth.dto.org;

import cn.cyberpeace.mp.auth.dto.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
public class OrgUserPageRequest extends PageRequest {

    @ApiModelProperty(value = "username 注意 后台会匹配 username 或者 name")
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
            super.setOrders(new String[]{"-update_time"});
        }
    }
}
