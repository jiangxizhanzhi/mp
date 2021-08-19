package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.cscene.bo.base.BaseCsceneBO;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import lombok.Data;

@Data
public class CsceneBO extends BaseCsceneBO<CsceneModel> {
    private Integer type;
    private String sceneConfig;

    @Override
    public CsceneModel createModel() {
        return new CsceneModel();
    }
}
