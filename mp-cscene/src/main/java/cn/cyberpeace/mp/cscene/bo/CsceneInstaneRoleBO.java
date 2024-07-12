 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneInstaneRoleBO.java.
 // File size 630.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.cscene.bo.base.BaseCsceneRoleBO;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceRoleModel;
import lombok.Data;

/**
 * @ClassName CsceneInstaneRoleBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCsceneRoleBO
 */
@Data
public class CsceneInstaneRoleBO extends BaseCsceneRoleBO<CsceneInstanceRoleModel, CsceneInstaneRoleBO> {
    /**
     * createModel
     *
     * @return
     */
    @Override
    public CsceneInstanceRoleModel createModel() {
        return new CsceneInstanceRoleModel();
    }
}
