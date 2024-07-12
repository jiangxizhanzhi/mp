 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SystemUser.java.
 // File size 996.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model.wazuh.syscollector;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("系统用户信息")
public class SystemUser {

    @ApiModelProperty(value = "备注")
    private String comment;

    @ApiModelProperty(value = "shell")
    private String shell;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "组名")
    private String groupName;

    @ApiModelProperty(value = "组ID")
    private String groupId;

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "home目录")
    private String dir;

    @ApiModelProperty(value = "时间")
    private LocalDateTime time;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "级别")
    private int level;
}
