package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.createtasks;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureContainer;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;

import java.util.List;

/**
 * Azure 容器创建任务
 */
public class AzureContainerCreateTask implements Runnable {

    private AzureContainer azureContainer;

    private CloudScenePluginHandler pluginHandler;

    private CloudPluginSceneElementBO element;

    private List<VirtualNic> virtualNics;


    public AzureContainerCreateTask(IAzureResourceManager resourceService,
                                    CloudScenePluginHandler pluginHandler,
                                    CloudPluginSceneElementBO element, List<VirtualNic> virtualNics) {
        this.azureContainer = new AzureContainer(resourceService);
        this.pluginHandler = pluginHandler;
        this.element = element;
        this.virtualNics = virtualNics;
    }

    @Override
    public void run() {
        azureContainer.create(this.pluginHandler, element, virtualNics);
    }

}
