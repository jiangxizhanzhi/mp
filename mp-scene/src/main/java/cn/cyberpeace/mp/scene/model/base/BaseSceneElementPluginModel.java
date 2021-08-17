package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import lombok.Data;

@Data
public class BaseSceneElementPluginModel extends BaseModel {
    private String scene;
    private String sceneElement;
    private String pluginKey;
}
