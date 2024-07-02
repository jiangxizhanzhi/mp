package cn.cyberpeace.mp.sceneplugincollection.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementModel;
import lombok.Data;

/**
 * @ClassName BaseCollectionPluginSceneElementModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePluginSceneElementModel
 */
@Data
public class BaseCollectionPluginSceneElementModel extends BasePluginSceneElementModel {
    @DefaultBool
    private Boolean enableProbeCollect;
    @DefaultStr
    private String probes;
}
