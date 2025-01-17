 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ModuleInfoResponse.java.
 // File size 570.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2023/1/17 16:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleInfoResponse {

    @ApiModelProperty(value = "模块名")
    private String moduleName;

    @ApiModelProperty(value = "模块是否激活 1:是 0:否")
    private Integer isActived;

}
