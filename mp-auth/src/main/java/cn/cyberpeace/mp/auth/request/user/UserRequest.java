 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserRequest.java.
 // File size 554.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.request.user;

import cn.cyberpeace.mp.auth.model.UserModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
@ApiModel("用户请求")
public class UserRequest extends UserModel {

    @ApiModelProperty(value = "组织id集合")
    List<String> orgIds;

    @ApiModelProperty(value = "角色id集合")
    List<String> roleIds;

}
