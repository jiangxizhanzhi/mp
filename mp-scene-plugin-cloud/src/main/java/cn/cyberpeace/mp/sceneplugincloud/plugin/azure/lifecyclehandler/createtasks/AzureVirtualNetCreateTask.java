package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.createtasks;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureVirtualNet;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Azure 虚拟网络创建任务
 */
public class AzureVirtualNetCreateTask implements Runnable {

    private AzureVirtualNet azureVirtualNet;

    private CloudScenePluginHandler pluginHandler;

    private CloudPluginSceneElementBO element;

    private ConcurrentHashMap<String, Boolean> cidrContainerMap;

    private HashMap<String, List<VirtualNic>> virtualNicsMap;

    public AzureVirtualNetCreateTask(IAzureResourceManager resourceService,
                                     CloudScenePluginHandler pluginHandler,
                                     CloudPluginSceneElementBO element,
                                     ConcurrentHashMap<String, Boolean> cidrContainerMap,
                                     HashMap<String, List<VirtualNic>> virtualNicsMap) {
        this.azureVirtualNet = new AzureVirtualNet(resourceService);
        this.pluginHandler = pluginHandler;
        this.element = element;
        this.cidrContainerMap = cidrContainerMap;
        this.virtualNicsMap = virtualNicsMap;
    }

    @Override
    public void run() {
        this.azureVirtualNet.create(this.pluginHandler, element, cidrContainerMap, virtualNicsMap);
    }

}
