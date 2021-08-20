package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.base.model.auth.BasePermissionModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Table("mp_devops_image_auth")
public class ImageAuthModel extends BasePermissionModel {

}
