 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserOnlineCntBo.java.
 // File size 457.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.bo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
@ApiModel
public class UserOnlineCntBo {

    @ApiModelProperty(value = "用户总数")
    private Integer totalUser;

    @ApiModelProperty(value = "在线用户数")
    private Integer onlineUser;

}
