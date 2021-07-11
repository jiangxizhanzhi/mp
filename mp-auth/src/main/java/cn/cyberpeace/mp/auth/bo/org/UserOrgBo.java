package cn.cyberpeace.mp.auth.bo.org;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("用户组织对象")
public class UserOrgBo extends OrgBO {

    
    @ApiModelProperty(value = "用户id")
    private String userId;

}
