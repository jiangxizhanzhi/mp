package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.createtasks;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureFireWall;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;

import java.util.List;

/**
 * Azure 防火墙 应用创建任务
 */
public class AzureFireWallCreateTask implements Runnable {

    private AzureFireWall azureFireWall;

    private CloudScenePluginHandler pluginHandler;

    private CloudPluginSceneElementBO element;
    private List<VirtualNic> virtualNics;


    public AzureFireWallCreateTask(IAzureResourceManager resourceService,
                                   CloudScenePluginHandler pluginHandler,
                                   CloudPluginSceneElementBO element,
                                   List<VirtualNic> virtualNics) {
        this.azureFireWall = new AzureFireWall(resourceService);
        this.pluginHandler = pluginHandler;
        this.element = element;
        this.virtualNics = virtualNics;
    }

    @Override
    public void run() {
        azureFireWall.create(this.pluginHandler, element, virtualNics);
    }

}
