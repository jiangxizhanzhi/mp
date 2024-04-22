package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.createtasks;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureVirtualNet;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;

import java.util.List;

/**
 * Azure 虚拟网络创建任务
 */
public class AzureVirtualNetCreateTask implements Runnable {

    private AzureVirtualNet azureVirtualNet;

    private CloudScenePluginHandler pluginHandler;

    private CloudPluginSceneElementBO element;

    private List<String> cidrList;

    public AzureVirtualNetCreateTask(IAzureResourceManager resourceService,
                                     CloudScenePluginHandler pluginHandler,
                                     CloudPluginSceneElementBO element, List<String> cidrList) {
        this.azureVirtualNet = new AzureVirtualNet(resourceService);
        this.pluginHandler = pluginHandler;
        this.element = element;
        this.cidrList = cidrList;
    }

    @Override
    public void run() {
        this.azureVirtualNet.create(this.pluginHandler, element, cidrList);
    }

}
