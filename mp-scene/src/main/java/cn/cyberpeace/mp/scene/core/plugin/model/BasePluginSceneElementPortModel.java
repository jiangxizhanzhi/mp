package cn.cyberpeace.mp.scene.core.plugin.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

@Data
public class BasePluginSceneElementPortModel extends BaseModel {
    @DefaultStr
    private String scene;
    @DefaultStr
    private String sceneElementPort;
    @DefaultBool
    private Boolean publicNetProxy;
    @DefaultStr
    private String publicNetProxyIp;
    @DefaultStr
    private String publicNetProxyPort;
}
