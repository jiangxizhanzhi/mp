package cn.cyberpeace.mp.sceneplugincloud.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneModel;
import lombok.Data;

/**
 * @ClassName BaseCloudPluginSceneModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePluginSceneModel
 */
@Data
public class BaseCloudPluginSceneModel extends BasePluginSceneModel {
    @DefaultBool
    private Boolean enableRecording;
    @DefaultBool
    private Boolean enableRate;
    @DefaultStr
    private String dnsNameservers;
}
