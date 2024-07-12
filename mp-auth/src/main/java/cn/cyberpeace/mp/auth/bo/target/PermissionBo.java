 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name PermissionBo.java.
 // File size 529.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.bo.target;

import cn.cyberpeace.mp.auth.model.TargetModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName PermissionBo
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see TargetModel
 */
@Data
public class PermissionBo extends TargetModel {

    @ApiModelProperty(value = "R/W/X")
    private String perm;

    @ApiModelProperty(value = "1: 正常 0：禁用")
    private Integer isEnabled;

}
