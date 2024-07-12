 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneAuthModel.java.
 // File size 485.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.authbase.model.BasePermissionModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CsceneAuthModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePermissionModel
 */
@Data
@NoArgsConstructor
@Table("mp_cscene_cscene_auth")
public class CsceneAuthModel extends BasePermissionModel {

}
