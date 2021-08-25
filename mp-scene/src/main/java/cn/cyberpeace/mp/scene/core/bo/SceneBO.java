package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.model.SceneModel;
import lombok.Data;

@Data
public class SceneBO extends BaseSceneBO<SceneModel, SceneBO> {
    private Integer event;
    private Integer eventStatus;
    private Integer process;
    private Integer processStatus;
    private String processMessage;

    @Override
    public SceneModel createModel() {
        return new SceneModel();
    }
}
