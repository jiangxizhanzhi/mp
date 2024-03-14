package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.deletetasks;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureContainer;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;

public class AzureContainerDeleteTask implements Runnable {

    private AzureContainer azureContainer;

    private CloudPluginSceneElementBO element;

    public AzureContainerDeleteTask(IAzureResourceManager resourceService,
                                    CloudPluginSceneElementBO element) {
        this.azureContainer = new AzureContainer(resourceService);
        this.element = element;
    }

    @Override
    public void run() {
        azureContainer.delete(element);
    }

}
