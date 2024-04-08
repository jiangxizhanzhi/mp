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
import com.azure.resourcemanager.containerregistry.models.AccessKeyType;
import com.azure.resourcemanager.containerregistry.models.Registry;
import com.azure.resourcemanager.containerregistry.models.RegistryCredentials;

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
        // 调用微软云api 创建虚拟机 回写虚拟机id
        // 虚拟机镜像、名称等信息
//        容器名中只能包含-字符
        String containerName = HandlerUtils.genResourceName(cloudElement.getSrcElement().getEid()).replace("_", "-").toLowerCase();
        String containerGroupName = cloudElement.getSrcElement().getEid();

//        TODO 注册表信息如何获取 是否只使用一个祖册表 多个注册表是否在同一个资源组 若不在容器镜像参数要包含资源组、注册表名、等信息
        Registry registrytest0407 = azureResourceManager.containerRegistries().getByResourceGroup("container-registry", "registrytest0407");
        RegistryCredentials acrCredentials = registrytest0407.getCredentials();
        String imageName = cloudElement.getImage();
//        String imageName = registrytest0407.loginServerUrl() + "/samples/" + "nginx";
        ContainerGroup container = azureResourceManager
                .containerGroups()
                .define(containerGroupName)
                .withRegion(this.region)
                .withNewResourceGroup(groupName)
                .withLinux()
//                .withPublicImageRegistryOnly()
                .withPrivateImageRegistry(registrytest0407.loginServerUrl(), acrCredentials.username(), acrCredentials.accessKeys().get(AccessKeyType.PRIMARY))
                .withoutVolume()
                .defineContainerInstance(containerName)
                .withImage(imageName)
//                TODO 前端未传先写死
                .withExternalTcpPort(80)
//                .withoutPorts()
//                TODO 设置网络
//                .withEnvironmentVariables(Collections.singletonList(new EnvironmentVariable().withName("KEY").withValue("VALUE")))
//                .withVolumeMountSetting("/mnt/myvolume1", "rw")
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
