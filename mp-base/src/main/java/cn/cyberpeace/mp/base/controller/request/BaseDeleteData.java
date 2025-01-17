 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseDeleteData.java.
 // File size 233.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:11 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseDeleteData {
    @ApiModelProperty(value = "数据id")
    private String id;
}
