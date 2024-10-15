 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysConfigModel.java.
 // File size 1011.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName SysConfigModel
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_sys_config")
public class SysConfigModel extends BaseModel {
    @ApiModelProperty(value = "租户")
    @DefaultStr("default")
    private String tenant;

    @ApiModelProperty(value = "配置名")
    private String keyName;

    @ApiModelProperty(value = "配置值")
    private String value;

    @ApiModelProperty(value = "初始值")
    private String initValue;
}
