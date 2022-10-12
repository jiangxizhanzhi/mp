package cn.cyberpeace.mp.auth.bo.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "角色用户数量统计信息")
@Data
public class UserRoleStatisticBo {

    @ApiModelProperty(value = "角色ID")
    private String roleId;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "用户数量")
    private Integer userCnt;

}
