 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WazuhEnvVarEventModel.java.
 // File size 771.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WazuhEnvVarEventModel {

    // 是否有变动，综合
    @JSONField(name = "has_changed")
    private Boolean hasChanged;

    @JSONField(name = "has_values")
    private Boolean hasValues;

    @JSONField(name = "has_deleted")
    private Boolean hasDeleted;

    @JSONField(name = "has_modified")
    private Boolean hasModified;

    @JSONField(name = "has_added")
    private Boolean hasAdded;

    @JSONField(name = "data")
    private String data;

}
