package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.base.model.auth.BasePermissionModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
@AllArgsConstructor
@Table("mp_auth_role_permission")
public class RolePermissionModel extends BasePermissionModel {
}
