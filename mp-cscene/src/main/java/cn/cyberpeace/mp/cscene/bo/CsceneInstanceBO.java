package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.cscene.bo.base.BaseCsceneBO;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import lombok.Data;

@Data
public class CsceneInstanceBO extends BaseCsceneBO<CsceneInstanceModel> {
    private String cscene;
    private Integer type;
    private String scene;

    @Override
    public CsceneInstanceModel createModel() {
        return new CsceneInstanceModel();
    }
}
