 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserOrgBo.java.
 // File size 480.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.bo.org;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName UserOrgBo
 * @Description
 * @Author qiaokang
 * @Date 2022/8/26 09:47
 * @Version 1.0.0
 * @see OrgBO
 */
@Data
@ApiModel("用户组织对象")
public class UserOrgBo extends OrgBO {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private String userId;

}
