package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.cscene.bo.base.BaseCsceneRoleBO;
import cn.cyberpeace.mp.cscene.model.CsceneRoleModel;
import lombok.Data;

@Data
public class CsceneRoleBO extends BaseCsceneRoleBO<CsceneRoleBO, CsceneRoleModel> {
    @Override
    public CsceneRoleModel createModel() {
        return new CsceneRoleModel();
    }
}
