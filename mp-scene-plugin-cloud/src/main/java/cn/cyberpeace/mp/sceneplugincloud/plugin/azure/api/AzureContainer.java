package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.ConnectedNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import com.azure.core.management.Region;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.containerinstance.models.ContainerGroup;
import com.azure.resourcemanager.containerinstance.models.ContainerGroupRestartPolicy;
import com.azure.resourcemanager.containerregistry.models.AccessKeyType;
import com.azure.resourcemanager.containerregistry.models.Registry;
import com.azure.resourcemanager.containerregistry.models.RegistryCredentials;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

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

        ContainerGroup container = creatContainer(cloudElement.getIsPublicIp(), cloudElement.getImage(), virtualNics,
                networkId, subnetName, groupName,
                containerName, containerGroupName, new HashMap(),
                cpu, ram);

        cloudElement.setResourceId(container.id());
        if (cloudElement.getIsPublicIp()) {
            cloudElement.setPublicIpAddress(container.ipAddress());
            virtualNic.setFixedIp(container.ipAddress());
        } else {
            virtualNic.setFixedIp(container.ipAddress());
        }
        receiveVirtualPortInfo(pluginHandler, cloudElement, virtualNics);
    }

    /**
     * 处理云场景无登录按钮、接收虚拟端口信息，用于云节点和虚拟网络接口的绑定及配置管理。
     *
     * @param cloudElement 云端场景节点，包含云端设备的配置信息。
     * @param virtualNics  虚拟网络接口列表，用于和云端设备进行网络连接。
     */
    public void receiveVirtualPortInfo(CloudScenePluginHandler pluginHandler, CloudPluginSceneElementBO cloudElement, List<VirtualNic> virtualNics) {
        if (virtualNics == null) {
            return;
        }

        var cloudPorts = pluginHandler.getPorts(cloudElement);
        if (cloudPorts.isEmpty()) {
            return;
        }

        Map<String, List<CloudPluginSceneElementPortBO>> networkIdCloudPortsMap = new HashMap<>();
        List<ConnectedNetworkBO> networks = new ArrayList<>();
        networks.addAll(cloudElement.getDeriveNetworks());
        networks.addAll(cloudElement.getAccessNetworks());
        for (ConnectedNetworkBO network : networks) {
            var auxiliaryNetworkId = network.getNetwork().getResourceId();
            var networkIdCloudPorts = networkIdCloudPortsMap.getOrDefault(
                    auxiliaryNetworkId, new ArrayList<>());
            network.getFromPorts().forEach(port -> {
                var pluginPort = pluginHandler.getPort(port.getId());
                networkIdCloudPorts.add(pluginPort);
            });
            networkIdCloudPortsMap.put(auxiliaryNetworkId, networkIdCloudPorts);
        }

        virtualNics.forEach(virtualNic -> {
            var networkId = virtualNic.getNetworkId();
            // 管理网卡判断
            if (Boolean.TRUE.equals(cloudElement.getEnableMgmt())
                    && CloudSingleton.cloudAppConfig.getMgmtNetId().equals(networkId)) {
                cloudElement.setMgmtPortResourceId(virtualNic.getPortId());
                cloudElement.setMgmtIp(virtualNic.getFixedIp());
                cloudElement.setMgmtMacAddr(virtualNic.getMacAddress());
                return;
            }

            var networkCloudPorts = networkIdCloudPortsMap.getOrDefault(networkId, null);
            if (networkCloudPorts == null) {
                return;
            }

            // 多个端口连到同一个网络上的时候暂时当做一个端口处理
            networkCloudPorts.forEach(cloudPort -> {
                var srcPort = cloudPort.getSrcPort();
                if (srcPort.isUsing()) {
                    srcPort.setIp(virtualNic.getFixedIp());
                    srcPort.setMacAddr(virtualNic.getMacAddress());
                }

                cloudPort.setResourceId(virtualNic.getPortId());
                cloudPort.setQosId(virtualNic.getQosId());
                cloudPort.setFloatingIp(virtualNic.getFloatingip());
                cloudPort.setFloatingIpId(virtualNic.getFloatingipId());
                cloudPort.setAuxiliaryNetworkId(virtualNic.getNetworkId());

                if (null != virtualNic.getAccessModes() && null != virtualNic.getAccessModes().get(0)) {
                    VirtualNic.AccessMode accessMode = virtualNic.getAccessModes().get(0);
                    String protocol = accessMode.getProtocol();
                    String[] protoclos = protocol.split(";");
                    List<CloudAccessMode> accessModes = cloudPort.getAccessModes();
                    accessModes.clear();
                    for (int j = 0; j < protoclos.length; j++) {
                        CloudAccessMode cloudAccessMode = new CloudAccessMode();
//                        TODO 管理网络ip port
                        cloudAccessMode.setAccessIp(virtualNic.getFixedIp());
                        cloudAccessMode.setProtocol(protoclos[j]);
                        cloudAccessMode.setAccessPort(AccessMode.Protocol.DEFAULT_PORT_MAP.get(protoclos[j].toLowerCase()));
                        cloudAccessMode.setUsername(cloudElement.getUsername());
                        cloudAccessMode.setPassword(cloudElement.getPassword());
                        cloudAccessMode.setAvailable(true);
                        cloudAccessMode.setProxy(true);
                        cloudAccessMode.setId(TextUtils.uuid());

                        if (StringUtils.isEmpty(cloudAccessMode.getAccessIp()) && cloudPort.getSrcPort() != null) {
                            cloudAccessMode.setAccessIp(cloudPort.getSrcPort().getIp());
                        }
                        accessModes.add(cloudAccessMode);
                    }
                }
            });
        });
    }

    public ContainerGroup creatContainer(Boolean isPublicIp, String image, List<VirtualNic> virtualNics,
                                         String networkId, String subnetName, String groupName,
                                         String containerName, String containerGroupName, Map environment,
                                         double cpu, double ram) {
        Registry registrytest0407 = azureResourceManager.containerRegistries().getByResourceGroup("upload_resources", "registry0530");
        RegistryCredentials acrCredentials = registrytest0407.getCredentials();
//        String imageName = registrytest0407.loginServerUrl() + "/" + image;

        ContainerGroup.DefinitionStages.WithNextContainerInstance attach = (ContainerGroup.DefinitionStages.WithNextContainerInstance) azureResourceManager
                .containerGroups()
                .define(containerName)
                .withRegion(this.region)
                .withNewResourceGroup(groupName)
                .withLinux()
                .withPrivateImageRegistry(registrytest0407.loginServerUrl(), acrCredentials.username(), acrCredentials.accessKeys().get(AccessKeyType.PRIMARY))
                .withoutVolume()
                .defineContainerInstance(containerGroupName)
                .withImage(image)
                .withExternalTcpPort(80)
                .withCpuCoreCount(cpu)
                .withMemorySizeInGB(ram)
                .withEnvironmentVariables(environment)
                .attach();
        ContainerGroup container;
        if (isPublicIp) {
            container = attach.withDnsPrefix("yourDNSPrefix-" + containerName.replaceAll("_", "")) // 这里指定DNS前缀
                    .withRestartPolicy(ContainerGroupRestartPolicy.NEVER)
                    .create();
        } else {
            container = attach.withNewNetworkProfileOnExistingVirtualNetwork(networkId, subnetName)
                    .withRestartPolicy(ContainerGroupRestartPolicy.NEVER)
                    .create();
        }
//        receiveVirtualPortInfo(element, virtualNics);


//        attach.withDnsPrefix("").d

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
