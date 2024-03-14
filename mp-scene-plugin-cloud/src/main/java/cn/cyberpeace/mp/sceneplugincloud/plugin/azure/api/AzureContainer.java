package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import com.azure.core.management.Region;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.containerinstance.models.ContainerGroup;

import java.util.Arrays;
import java.util.List;

public class AzureContainer {
    private IAzureResourceManager resourceService;

    private AzureResourceManager azureResourceManager;

    private Region region;

    public AzureContainer(IAzureResourceManager resourceService) {
        this.resourceService = resourceService;
        this.azureResourceManager = resourceService.getManager();
        this.region = resourceService.getRegin();
    }

    public void create(CloudScenePluginHandler pluginHandler, CloudPluginSceneElementBO cloudElement, List<VirtualNic> virtualNics) {
        String groupName = pluginHandler.getSourceSceneId();

        // azure todo 调用微软云api 查询或者创建一个 flavor
        String flavor = "";

        // 调用微软云api 创建虚拟机 回写虚拟机id
        // 虚拟机镜像、名称等信息
        String containerName = HandlerUtils.genResourceName(cloudElement.getSrcElement().getEid());
        // azure todo 根据imageName 获取 对应的 image
        String image = cloudElement.getImage();

        String containerGroupName = cloudElement.getSrcElement().getEid();
        var container = azureResourceManager.containerGroups().define(containerGroupName)
                .withRegion(this.region)
                .withNewResourceGroup(groupName)
                .withLinux()
                .withPublicImageRegistryOnly()
                .withoutVolume()
                .defineContainerInstance(containerName)
                    .withImage(image)
                    .withoutPorts()
                    .withCpuCoreCount(cloudElement.getSrcElement().getCpu())
                    .withMemorySizeInGB(cloudElement.getSrcElement().getRam())
                .attach()
                .create();

        // azure todo  怎么添加网卡呢?
        for (var virtualNic : virtualNics) {
            // azure todo  怎么添加网卡呢?
        }

        cloudElement.setResourceId(container.id());
    }

    public void delete(CloudPluginSceneElementBO cloudElement) {
        azureResourceManager.containerGroups().deleteById(cloudElement.getResourceId());
    }

    public void execCommand(CloudPluginSceneElementBO element, String command) {
        String containerGroupName = element.getSrcElement().getEid();
        azureResourceManager.containerGroups()
                .getById(containerGroupName).containers()
                .get(element.getResourceId())
                .withCommand(Arrays.asList(command));
    }
}
