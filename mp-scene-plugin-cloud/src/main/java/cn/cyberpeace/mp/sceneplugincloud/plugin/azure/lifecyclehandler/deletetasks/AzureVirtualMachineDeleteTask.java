package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.lifecyclehandler.deletetasks;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api.AzureVirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;

public class AzureVirtualMachineDeleteTask implements Runnable {

    private AzureVirtualMachine azureVirtualMachine;

    private CloudPluginSceneElementBO element;

    public AzureVirtualMachineDeleteTask(IAzureResourceManager resourceService,
                                         CloudPluginSceneElementBO element) {
        this.azureVirtualMachine = new AzureVirtualMachine(resourceService);
        this.element = element;
    }

    @Override
    public void run() {
        azureVirtualMachine.delete(element);
    }

}
