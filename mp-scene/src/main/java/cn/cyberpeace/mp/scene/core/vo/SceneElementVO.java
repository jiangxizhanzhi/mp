package cn.cyberpeace.mp.scene.core.vo;

import cn.cyberpeace.mp.scene.core.vo.base.BaseSceneElementVO;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import lombok.Data;

import java.util.List;

@Data
public class SceneElementVO extends BaseSceneElementVO<SceneElementModel> {
    private Integer event;
    private Integer eventStatus;
    private Integer process;
    private Integer processStatus;
    private Integer processMessage;

    private List<SceneElementPortVO> ports;

    @Override
    public SceneElementModel createModel() {
        return new SceneElementModel();
    }

}
