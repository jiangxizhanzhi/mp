package cn.cyberpeace.mp.sceneplugingns3.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementModel;
import lombok.Data;

@Data
public class BaseGns3PluginSceneElementModel extends BasePluginSceneElementModel {
    @DefaultStr
    private String staticRoutes;
    @DefaultStr
    private String image;
    @DefaultStr
    private String platform;
    @DefaultInt
    private Integer ram;
}
