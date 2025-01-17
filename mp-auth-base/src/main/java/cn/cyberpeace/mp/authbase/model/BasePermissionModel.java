// Copyright (C) 2024
// All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BasePermissionModel.java.
 // File size 639.
 // File create date Fri Jan 17 09:57:14 CST 2025.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.authbase.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

 /**
  * 基本权限模型
  *
  * @author 二竹
  * @date 2025/01/17
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
