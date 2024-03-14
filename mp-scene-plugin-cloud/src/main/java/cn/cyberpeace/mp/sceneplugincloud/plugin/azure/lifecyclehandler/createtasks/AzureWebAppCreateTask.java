package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.createtasks;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureWebApp;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;

import java.util.List;

public class AzureWebAppCreateTask implements Runnable {

    private AzureWebApp azureWebApp;

    private CloudScenePluginHandler pluginHandler;

    private CloudPluginSceneElementBO element;

    private List<VirtualNic> virtualNics;


    public AzureWebAppCreateTask(IAzureResourceManager resourceService,
                                 CloudScenePluginHandler pluginHandler,
                                 CloudPluginSceneElementBO element, List<VirtualNic> virtualNics) {
        this.azureWebApp = new AzureWebApp(resourceService);
        this.pluginHandler = pluginHandler;
        this.element = element;
        this.virtualNics = virtualNics;
    }

    @Override
    public void run() {
        azureWebApp.create(this.pluginHandler, element, virtualNics);
    }

}
