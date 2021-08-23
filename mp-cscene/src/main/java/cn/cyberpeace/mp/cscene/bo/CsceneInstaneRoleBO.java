package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.cscene.bo.base.BaseCsceneRoleBO;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceRoleModel;
import lombok.Data;

@Data
public class CsceneInstaneRoleBO extends BaseCsceneRoleBO<CsceneInstaneRoleBO, CsceneInstanceRoleModel> {
    @Override
    public CsceneInstanceRoleModel createModel() {
        return new CsceneInstanceRoleModel();
    }
}
