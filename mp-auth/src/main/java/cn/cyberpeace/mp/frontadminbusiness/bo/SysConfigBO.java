 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysConfigBO.java.
 // File size 843.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadminbusiness.bo;

import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName SysConfigBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ModelBO
 */
@Data
public class SysConfigBO extends ModelBO<SysConfigModel, SysConfigBO> {
    @ApiModelProperty(value = "租户")
    private String tenant;

    @ApiModelProperty(value = "配置名")
    private String keyName;

    @ApiModelProperty(value = "配置值")
    private String value;

    @ApiModelProperty(value = "初始值")
    private String initValue;

    @Override
    public SysConfigModel createModel() {
        return new SysConfigModel();
    }
}
