package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.deletetasks;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureVirtualNet;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;

public class AzureVirtualNetDeleteTask implements Runnable {

    private AzureVirtualNet azureVirtualNet;

    private CloudPluginSceneElementBO element;

    public AzureVirtualNetDeleteTask(IAzureResourceManager resourceService,
                                     CloudPluginSceneElementBO element) {
        this.azureVirtualNet = new AzureVirtualNet(resourceService);
        this.element = element;
    }

    @Override
    public void run() {
        azureVirtualNet.delete(element);
    }

}
