 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name QuickCreateParam.java.
 // File size 842.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName QuickCreateParam
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
public class QuickCreateParam {
    @ApiModelProperty(value = "用户uid")
    private String installerUuid;
    @ApiModelProperty(value = "快速构建启动参数")
    private List<StartUpParameter> settings;
    @ApiModelProperty(value = "用户名")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "头像")
    private String logo;
    @ApiModelProperty(value = "file")
    private String file;
}
