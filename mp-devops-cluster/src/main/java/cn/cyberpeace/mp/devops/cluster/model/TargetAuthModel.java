package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.base.model.auth.BasePermissionModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table("mp_devops_target_auth")
public class TargetAuthModel extends BasePermissionModel {

}
