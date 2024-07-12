 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WazuhSystemUserModel.java.
 // File size 911.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WazuhSystemUserModel {

    @JSONField(name = "comment")
    private String comment;

    @JSONField(name = "shell")
    private String shell;

    @JSONField(name = "name")
    private String name;

    @JSONField(name = "group_name")
    private String groupName;

    @JSONField(name = "group_id")
    private String groupId;

    @JSONField(name = "id")
    private String id;

    @JSONField(name = "dir")
    private String dir;

    private String description;

    private LocalDateTime time;

    @JSONField(name = "level")
    private int level;
}
