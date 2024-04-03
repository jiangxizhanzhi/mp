package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import com.azure.core.management.Region;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.containerinstance.models.ContainerGroup;
import com.azure.resourcemanager.containerinstance.models.ContainerGroupRestartPolicy;

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
//        容器名中只能包含-字符
        String containerName = HandlerUtils.genResourceName(cloudElement.getSrcElement().getEid()).replace("_", "-").toLowerCase();
        // azure todo 根据imageName 获取 对应的 image
        String image = cloudElement.getImage();
        final String containerImageName1 = "mcr.microsoft.com/azuredocs/aci-helloworld";
        String containerGroupName = cloudElement.getSrcElement().getEid();
       /* var container = azureResourceManager.containerGroups().define(containerGroupName)
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
                .create();*/

        ContainerGroup container = azureResourceManager.containerGroups().define(containerGroupName)
                .withRegion(this.region)
                .withNewResourceGroup(groupName)
                .withLinux()
                .withPublicImageRegistryOnly()
                .withoutVolume()
                .defineContainerInstance(containerName)
                .withImage(containerImageName1)
                .withExternalTcpPort(80)
//                .withoutPorts()
//                TODO 前端未传先写死
                .withCpuCoreCount(0.5)
                .withMemorySizeInGB(0.8)
                .attach()
                .withRestartPolicy(ContainerGroupRestartPolicy.NEVER)
                .withDnsPrefix(containerGroupName)
                .create();

        // azure todo  怎么添加网卡呢?
        for (var virtualNic : virtualNics) {
            // azure todo  怎么添加网卡呢?
        }

        cloudElement.setResourceId(container.id());
    }

    public void delete(String instanceId) {
//        todo 若是一个容器分配一个容器组 可直接删除容器组
        azureResourceManager.containerGroups()
                .deleteById(instanceId);
    }

    public void execCommand(CloudPluginSceneElementBO element, String command) {
        String containerGroupName = element.getSrcElement().getEid();
        azureResourceManager.containerGroups()
                .getById(containerGroupName).containers()
                .get(element.getResourceId())
                .withCommand(Arrays.asList(command));
    }

    public void pause(CloudPluginSceneElementBO element) {
        String containerGroupName = element.getSrcElement().getEid();
        ContainerGroup containerGroup = azureResourceManager.containerGroups()
                .getById(containerGroupName);
        containerGroup.stop();
    }

    public void restart(String instanceId) {
        ContainerGroup containerGroup = azureResourceManager.containerGroups()
                .getById(instanceId);
        containerGroup.restart();
    }

    public void start(String instanceId) {
        ContainerGroup containerGroup = azureResourceManager.containerGroups()
                .getById(instanceId);
        azureResourceManager.containerGroups().start(containerGroup.resourceGroupName(), containerGroup.name());
    }

    public void unpause(CloudPluginSceneElementBO element) {
        String containerGroupName = element.getSrcElement().getEid();
        ContainerGroup containerGroup = azureResourceManager.containerGroups()
                .getById(containerGroupName);
        containerGroup.restart();
    }

    public void stop(String instanceId) {
        ContainerGroup containerGroup = azureResourceManager.containerGroups()
                .getById(instanceId);
        containerGroup.stop();
    }
}
