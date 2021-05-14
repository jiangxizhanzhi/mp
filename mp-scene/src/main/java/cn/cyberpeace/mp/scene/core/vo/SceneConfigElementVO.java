package cn.cyberpeace.mp.scene.core.vo;

import cn.cyberpeace.mp.scene.core.vo.base.BaseSceneElementVO;
import cn.cyberpeace.mp.scene.model.SceneConfigElementModel;
import lombok.Data;

import java.util.List;

@Data
public class SceneConfigElementVO extends BaseSceneElementVO<SceneConfigElementModel> {

    private List<SceneConfigElementPortVO> ports;

    @Override
    public SceneConfigElementModel createModel() {
        return new SceneConfigElementModel();
    }

}
