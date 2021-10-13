package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.core.plugin.PluginSceneEventHandlerCommon;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public class HandlerCommon extends PluginSceneEventHandlerCommon<CloudScenePluginHandler> {

    @Getter
    private HandlerUtils<CloudScenePluginHandler> handlerUtils;

    public HandlerCommon(CloudScenePluginHandler pluginHandler, TaskPool taskPool, int event) {
        super(pluginHandler, taskPool, event);
        handlerUtils = pluginHandler.getHandlerUtils();
    }

    public String genResourceName(String name) {
        return StringUtils.joinWith("_", CloudSingleton.cloudAppConfig.getCpcloudResourceGroup(), name);
    }

    public String getRequestUser() {
        var context = getPluginHandler().getContext();
        if (context == null) {
            return null;
        }

        return context.getRequestUser();
    }

}
