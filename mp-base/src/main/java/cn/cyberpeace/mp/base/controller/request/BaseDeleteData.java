 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseDeleteData.java.
 // File size 362.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName BaseDeleteData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Deprecated
@Data
public class BaseDeleteData {
    @ApiModelProperty(value = "数据id")
    private String id;
}
