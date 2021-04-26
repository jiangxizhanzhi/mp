package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.model.base.BasePermissionModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Table("mp_auth_role_permission")
public class RolePermissionModel extends BasePermissionModel {
}
