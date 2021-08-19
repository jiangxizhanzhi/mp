package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.base.model.auth.BasePermissionModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Table("mp_auth_org_permission")
public class OrgPermissionModel extends BasePermissionModel {
}
