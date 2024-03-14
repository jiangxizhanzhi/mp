package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import com.azure.core.management.Region;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.appservice.models.PricingTier;

import java.util.List;

public class AzureWebApp {
    private IAzureResourceManager resourceService;

    private AzureResourceManager azureResourceManager;

    private Region region;

    public AzureWebApp(IAzureResourceManager resourceService) {
        this.resourceService = resourceService;
        this.azureResourceManager = resourceService.getManager();
        this.region = resourceService.getRegin();
    }

    public void create(CloudScenePluginHandler pluginHandler, CloudPluginSceneElementBO cloudElement, List<VirtualNic> virtualNics) {
        String groupName = pluginHandler.getSourceSceneId();
        azureResourceManager.webApps().define("")
                .withRegion(this.region)
                .withExistingResourceGroup(groupName)
                .withNewLinuxPlan("", new PricingTier("", ""))
                .withPrivateRegistryImage("", "")
                .withCredentials("root", "root")
                .withStartUpCommand("")
                .create();
    }

    public void delete(CloudPluginSceneElementBO cloudElement) {
        azureResourceManager.webApps().deleteById(cloudElement.getResourceId());
    }
}
