package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.createtasks;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureWebApp;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;

import java.util.List;

/**
 * Azure Web 应用创建任务
 */
public class AzureWebAppCreateTask implements Runnable {

    private AzureWebApp azureWebApp;

    private CloudScenePluginHandler pluginHandler;

    private CloudPluginSceneElementBO element;

    private List<VirtualNic> virtualNics;

    private String groupName;


    public AzureWebAppCreateTask(IAzureResourceManager resourceService,
                                 CloudScenePluginHandler pluginHandler,
                                 CloudPluginSceneElementBO element,
                                 List<VirtualNic> virtualNics,
                                 String groupName) {
        this.azureWebApp = new AzureWebApp(resourceService);
        this.pluginHandler = pluginHandler;
        this.element = element;
        this.virtualNics = virtualNics;
        this.groupName = groupName;
    }

    @Override
    public void run() {
        azureWebApp.create(this.pluginHandler, element, virtualNics, groupName);
    }

}
