package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.createtasks;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureVirtualNet;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Azure 虚拟网络创建任务
 */
public class AzureVirtualNetCreateTask implements Runnable {

    private AzureVirtualNet azureVirtualNet;

    private CloudScenePluginHandler pluginHandler;

    private CloudPluginSceneElementBO element;

    private ConcurrentHashMap<String, Boolean> cidrContainerMap;

    public AzureVirtualNetCreateTask(IAzureResourceManager resourceService,
                                     CloudScenePluginHandler pluginHandler,
                                     CloudPluginSceneElementBO element,
                                     ConcurrentHashMap<String, Boolean> cidrContainerMap) {
        this.azureVirtualNet = new AzureVirtualNet(resourceService);
        this.pluginHandler = pluginHandler;
        this.element = element;
        this.cidrContainerMap = cidrContainerMap;
    }

   @Override
    public void run() {
        this.azureVirtualNet.create(this.pluginHandler, element, cidrContainerMap);
    }

}
