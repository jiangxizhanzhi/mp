 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysVersionResponse.java.
 // File size 710.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.response;

import cn.cyberpeace.mp.frontadmin.model.SysModelVersionModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName SysVersionResponse
 * @Description
 * @Author guquan
 * @Date 2023/9/15 14:53
 * @Version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysVersionResponse {

    @ApiModelProperty(value = "系统版本")
    SysModelVersionModel sysVersion;

    @ApiModelProperty(value = "模块版本")
    List<SysModelVersionModel> modelVersions;

}
