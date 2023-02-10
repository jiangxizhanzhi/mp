package cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler;

import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ElementCreateHandler extends HandlerCommon {

    public static class SceneProcess {
    }

    public static class ElementProcess {
    }

    public ElementCreateHandler(CollectionScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, CreateTaskPool.getInstance(), event);
    }

    public void createElement(CollectionPluginSceneElementBO element) {
        var flowCollectorId = getPluginHandler().getScene().getFlowCollector();
        if (StringUtils.isNotEmpty(flowCollectorId)) {
            var sceneId = getPluginHandler().getSrcScene().getEid();
            Set<String> vmInstanceIds = new HashSet<>();
            Set<String> containerInstanceIds = new HashSet<>();
            CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler().getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
            if (cloudPluginHandler != null) {
                var cloudElement = cloudPluginHandler.getElement(element.getSrcElement().getId());
                if (cloudElement != null && cloudElement.getVirtualType() != null
                        && !StringUtils.isEmpty(cloudElement.getResourceId())) {
                    if (cloudElement.getVirtualType().equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)) {
                        vmInstanceIds.add(cloudElement.getResourceId());
                    } else if (cloudElement.getVirtualType().equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)) {
                        containerInstanceIds.add(cloudElement.getResourceId());
                    }
                }
                if (!vmInstanceIds.isEmpty() || !containerInstanceIds.isEmpty()) {
                    getFlowCollector().bindNetworkPorts(sceneId, flowCollectorId, new ArrayList<>(vmInstanceIds), new ArrayList<>(containerInstanceIds));
                }
            }
        }
    }

}
