package cn.cyberpeace.mp.scene.core.vo;

import cn.cyberpeace.mp.scene.core.vo.base.BaseSceneVO;
import cn.cyberpeace.mp.scene.model.SceneModel;
import lombok.Data;

@Data
public class SceneVO extends BaseSceneVO<SceneModel> {
    private Integer event;
    private Integer eventStatus;
    private Integer process;
    private Integer processStatus;
    private Integer processMessage;

    @Override
    public SceneModel createModel() {
        return new SceneModel();
    }
}
