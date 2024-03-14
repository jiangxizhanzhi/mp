package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import com.azure.core.management.Region;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.KnownLinuxVirtualMachineImage;
import com.azure.resourcemanager.compute.models.KnownWindowsVirtualMachineImage;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.compute.models.VirtualMachineSizeTypes;
import com.azure.resourcemanager.network.models.Network;
import com.azure.resourcemanager.network.models.NetworkInterface;

import java.util.ArrayList;
import java.util.List;

public class AzureVirtualMachine {
    private IAzureResourceManager resourceService;

    private AzureResourceManager azureResourceManager;

    private Region region;

    public AzureVirtualMachine(IAzureResourceManager resourceService) {
        this.resourceService = resourceService;
        this.azureResourceManager = resourceService.getManager();
        this.region = resourceService.getRegin();
    }

    public void create(CloudScenePluginHandler pluginHandler, CloudPluginSceneElementBO cloudElement, List<VirtualNic> virtualNics) {
        String groupName = pluginHandler.getSourceSceneId();

        // azure todo 调用微软云api 查询或者创建一个 flavor
        VirtualMachineSizeTypes flavor = VirtualMachineSizeTypes.STANDARD_B1S;

        // 调用微软云api 创建虚拟机 回写虚拟机id
        // 虚拟机镜像、名称等信息
        String vmName = HandlerUtils.genResourceName(cloudElement.getSrcElement().getEid());
        String imageName = cloudElement.getImage();
        // azure todo 根据imageName 获取 对应的 image
        KnownLinuxVirtualMachineImage image = KnownLinuxVirtualMachineImage.UBUNTU_SERVER_16_04_LTS;

        // 获取网卡的信息

        // 取第一块网卡
        // azure todo 调整 root name
        VirtualNic virtualNic = virtualNics.get(0);
        VirtualMachine.DefinitionStages.WithCreate creater = resourceService.getManager().virtualMachines()
                .define(vmName)
                .withRegion(region)
                .withNewResourceGroup(groupName)
                .withExistingPrimaryNetwork(azureResourceManager.networks().getById(virtualNic.getNetworkId()))
                .withSubnet("sub")
                .withPrimaryPrivateIPAddressStatic(virtualNic.getFixedIp())
                .withoutPrimaryPublicIPAddress()
                .withPopularLinuxImage(image)
                .withRootUsername("root1")
                .withRootPassword("ycxx123#") // 必须要是6位以上,且有一个特殊字符
                .withUnmanagedDisks()
                .defineUnmanagedDataDisk("disk-1")
                .withNewVhd(cloudElement.getSrcElement().getDisk())
                .withLun(1)
                .attach()
                .withSize(flavor);

        // 加其他网卡
        for (int i = 1; i < virtualNics.size(); i++) {
            VirtualNic nic = virtualNics.get(i);
            String netId = virtualNics.get(i).getNetworkId();
            Network azureNetwork = azureResourceManager.networks().getById(netId);
            NetworkInterface ni = azureResourceManager.networkInterfaces()
                    .define("nic" + i)
                    .withRegion(region)
                    .withNewResourceGroup(groupName)
                    .withExistingPrimaryNetwork(azureNetwork)
                    .withSubnet("sub")
                    .withPrimaryPrivateIPAddressStatic(nic.getFixedIp())
                    .create();
            creater.withExistingSecondaryNetworkInterface(ni);
        }
        // azure todo 创建虚拟机 回写数据
        VirtualMachine vm = creater.create();
        cloudElement.setResourceId(vm.id());
    }

    public void delete(CloudPluginSceneElementBO cloudElement) {
        azureResourceManager.virtualMachines().deleteById(cloudElement.getResourceId());
    }

    public void execCommand(String group, String vmName, List<String> commands) {
        azureResourceManager.virtualMachines().runShellScript(group, vmName, commands, new ArrayList<>());
    }
}
