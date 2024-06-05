package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import com.azure.core.management.Region;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.containerinstance.models.ContainerGroup;
import com.azure.resourcemanager.containerinstance.models.ContainerGroupRestartPolicy;
import com.azure.resourcemanager.containerregistry.models.AccessKeyType;
import com.azure.resourcemanager.containerregistry.models.Registry;
import com.azure.resourcemanager.containerregistry.models.RegistryCredentials;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        VirtualNic virtualNic = virtualNics.get(0);
        String networkId = virtualNic.getNetworkId();
        String fixedIp = virtualNic.getFixedIp();
        String subnetName = IpUtils.extractNetworkPart(fixedIp);

        String groupName = pluginHandler.getSourceSceneId();
        // 调用微软云api 创建虚拟机 回写虚拟机id
        // 虚拟机镜像、名称等信息
//        容器名中只能包含-字符
        String containerName = HandlerUtils.genResourceName(cloudElement.getSrcElement().getEid());
        String containerGroupName = cloudElement.getSrcElement().getEid().replace("_", "-").toLowerCase();

        var srcElement = cloudElement.getSrcElement();

        double cpu = srcElement.getCpu() == null ? 1 : srcElement.getCpu();
        double ram = srcElement.getRam() == null ? 1.5 : srcElement.getRam();

        ContainerGroup container = creatContainer(cloudElement.getImage(), virtualNics,
                networkId, subnetName, groupName,
                containerName, containerGroupName, new HashMap(),
                cpu, ram);

        cloudElement.setResourceId(container.id());
    }

    public ContainerGroup creatContainer(String image, List<VirtualNic> virtualNics, String networkId,
                                         String subnetName, String groupName, String containerName,
                                         String containerGroupName, Map environment,
                                         double cpu, double ram) {
        //        TODO 注册表信息如何获取 是否只使用一个祖册表 多个注册表是否在同一个资源组 若不在容器镜像参数要包含资源组、注册表名、等信息
        Registry registrytest0407 = azureResourceManager.containerRegistries().getByResourceGroup("container_registry0530", "registry0530");
        RegistryCredentials acrCredentials = registrytest0407.getCredentials();
//        String imageName = cloudElement.getImage();
//        String imageName = registrytest0407.loginServerUrl() + "/samples/nginx";
        String imageName = registrytest0407.loginServerUrl() + "/" + image;

        ContainerGroup.DefinitionStages.WithNextContainerInstance attach = (ContainerGroup.DefinitionStages.WithNextContainerInstance) azureResourceManager
                .containerGroups()
                .define(containerName)
                .withRegion(this.region)
                .withNewResourceGroup(groupName)
                .withLinux()
                .withPrivateImageRegistry(registrytest0407.loginServerUrl(), acrCredentials.username(), acrCredentials.accessKeys().get(AccessKeyType.PRIMARY))
                .withoutVolume()
                .defineContainerInstance(containerGroupName)
//                TODO 前端未传先写死
                .withImage(imageName)
                .withExternalTcpPort(80)
                .withCpuCoreCount(cpu)
                .withMemorySizeInGB(ram)
                .withEnvironmentVariables(environment)
                .attach();
        ContainerGroup container = attach.withNewNetworkProfileOnExistingVirtualNetwork(networkId, subnetName)
                .withRestartPolicy(ContainerGroupRestartPolicy.NEVER)
                .create();

        // azure todo  怎么添加网卡呢?
/*        for (var virtualNic1 : virtualNics) {
            // azure todo  怎么添加网卡呢?
        }*/
        return container;
    }

    public void delete(String instanceId) {
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
