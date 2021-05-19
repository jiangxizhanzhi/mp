package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import lombok.Data;

import java.util.List;

@Data
public class SceneElementBO extends BaseSceneElementBO<SceneElementModel> {
    private Integer event;
    private Integer eventStatus;
    private Integer process;
    private Integer processStatus;
    private Integer processMessage;

    private List<SceneElementPortBO> ports;

    @Override
    public SceneElementModel createModel() {
        return new SceneElementModel();
    }

}
