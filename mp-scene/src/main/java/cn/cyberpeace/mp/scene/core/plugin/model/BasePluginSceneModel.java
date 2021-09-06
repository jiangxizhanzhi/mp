package cn.cyberpeace.mp.scene.core.plugin.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

@Data
public class BasePluginSceneModel extends BaseModel {
    @DefaultStr
    private String scene;
}
