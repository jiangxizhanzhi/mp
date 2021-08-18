package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.cscene.bo.base.BaseCsceneRoleBO;
import cn.cyberpeace.mp.cscene.model.CsceneRoleModel;
import lombok.Data;

@Data
public class CsceneRoleBO extends BaseCsceneRoleBO<CsceneRoleModel> {
    private String cscene;

    @Override
    public CsceneRoleModel createModel() {
        return new CsceneRoleModel();
    }
}
