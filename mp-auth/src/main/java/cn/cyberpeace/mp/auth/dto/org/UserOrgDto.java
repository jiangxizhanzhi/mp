package cn.cyberpeace.mp.auth.dto.org;

import cn.cyberpeace.mp.auth.model.OrgModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 用户组织对象
 * @Author qiaokang
 * @Date：2022/8/26 09:47
 */
@Data
@ApiModel("用户组织对象")
public class UserOrgDto extends OrgModel {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private String userId;

}
