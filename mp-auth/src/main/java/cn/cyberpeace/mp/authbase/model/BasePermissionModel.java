 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BasePermissionModel.java.
 // File size 779.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.authbase.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

/**
 * @ClassName BasePermissionModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
public abstract class BasePermissionModel extends BaseModel {
    @DefaultStr("default")
    private String tenant;
    private String owner;
    private String target;
    @DefaultBool(true)
    private Boolean canRead;
    @DefaultBool(true)
    private Boolean canCreate;
    @DefaultBool(true)
    private Boolean canUpdate;
    @DefaultBool(true)
    private Boolean canDelete;
}
