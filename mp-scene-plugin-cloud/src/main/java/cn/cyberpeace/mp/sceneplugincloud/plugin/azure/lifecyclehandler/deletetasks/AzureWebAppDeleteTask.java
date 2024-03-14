package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.deletetasks;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureWebApp;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;

public class AzureWebAppDeleteTask implements Runnable {

    private AzureWebApp azureWebApp;

    private CloudPluginSceneElementBO element;

    public AzureWebAppDeleteTask(IAzureResourceManager resourceService,
                                 CloudPluginSceneElementBO element) {
        this.azureWebApp = new AzureWebApp(resourceService);
        this.element = element;
    }

    @Override
    public void run() {
        azureWebApp.delete(element);
    }

}
