package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.authbase.model.BasePermissionModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName UserPermissionModel
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BasePermissionModel
 */
@Data
@AllArgsConstructor
@Table("mp_auth_user_permission")
public class UserPermissionModel extends BasePermissionModel {

}
