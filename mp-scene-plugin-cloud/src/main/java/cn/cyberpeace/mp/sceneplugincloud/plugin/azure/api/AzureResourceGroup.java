package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api;

import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import com.azure.core.management.Region;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.resources.models.ResourceGroup;

public class AzureResourceGroup {
    private IAzureResourceManager resourceService;

    private AzureResourceManager azureResourceManager;

    private Region region;

    public AzureResourceGroup(IAzureResourceManager resourceService) {
        this.resourceService = resourceService;
        this.azureResourceManager = resourceService.getManager();
        this.region = resourceService.getRegin();
    }

    public String create(String groupName){
        ResourceGroup resourceGroup = azureResourceManager.resourceGroups().define(groupName)
                .withRegion(region)
                .create();
        return resourceGroup.id();

    }
}
