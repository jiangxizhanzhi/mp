 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserOrgBo.java.
 // File size 443.
 // File create date Fri Jan 17 09:57:14 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.bo.org;

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
public class UserOrgBo extends OrgBO {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private String userId;

}
