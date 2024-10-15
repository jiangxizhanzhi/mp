 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RolePermissionModel.java.
 // File size 567.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.base.model.BasePermissionModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName RolePermissionModel
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BasePermissionModel
 */
@Data
@AllArgsConstructor
@Table("mp_auth_role_permission")
public class RolePermissionModel extends BasePermissionModel {
    public static final String QUERY_FIELD_TARGET = "target";
}
