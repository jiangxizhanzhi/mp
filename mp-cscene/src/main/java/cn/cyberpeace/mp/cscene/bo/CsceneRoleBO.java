package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.cscene.bo.base.BaseCsceneRoleBO;
import cn.cyberpeace.mp.cscene.model.CsceneRoleModel;
import lombok.Data;

/**
 * @ClassName CsceneRoleBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCsceneRoleBO
 */
@Data
public class CsceneRoleBO extends BaseCsceneRoleBO<CsceneRoleModel, CsceneRoleBO> {
    /**
     * createModel
     *
     * @return
     */
    @Override
    public CsceneRoleModel createModel() {
        return new CsceneRoleModel();
    }
}
