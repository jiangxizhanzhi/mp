package cn.cyberpeace.mp.sceneplugincloud.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneModel;
import lombok.Data;

@Data
public class BaseCloudPluginSceneModel extends BasePluginSceneModel {
    @DefaultBool
    private Boolean enableRecording;
    @DefaultBool
    private Boolean enableRate;

    @DefaultStr
    private String internalVmId;
    @DefaultStr
    private String internalVmIp;
    @DefaultStr
    private String internalRouterId;
    @DefaultStr
    private String internalNetId;

    @DefaultBool(value = true)
    private Boolean createInternalVm;
}
