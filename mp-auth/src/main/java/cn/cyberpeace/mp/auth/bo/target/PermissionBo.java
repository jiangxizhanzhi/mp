// Copyright (C) 2024
// All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name PermissionBo.java.
 // File size 464.
 // File create date Fri Jan 17 09:57:14 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.bo.target;

import cn.cyberpeace.mp.auth.model.TargetModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

 /**
  * 允许薄熙来
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Data
public class PermissionBo extends TargetModel {

    @ApiModelProperty(value = "R/W/X")
    private String perm;

    @ApiModelProperty(value = "1: 正常 0：禁用")
    private Integer isEnabled;

}
