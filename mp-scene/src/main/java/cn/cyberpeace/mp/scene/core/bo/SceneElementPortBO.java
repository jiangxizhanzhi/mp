package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.model.SceneElementPortModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class SceneElementPortBO extends BaseSceneElementPortBO<SceneElementPortModel, SceneElementPortBO, SceneElementBO> {

    @JsonIgnore
    private boolean using = true;

    @Override
    public SceneElementPortModel createModel() {
        return new SceneElementPortModel();
    }

}
