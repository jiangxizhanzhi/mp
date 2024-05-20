package cn.cyberpeace.mp.sceneplugincloud.plugin.azure.api;

import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.management.Region;
import com.azure.core.util.Context;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.fluent.models.ResourceSkuInner;
import com.azure.resourcemanager.compute.models.*;
import com.azure.resourcemanager.network.models.Network;
import com.azure.resourcemanager.network.models.NetworkInterface;
import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class AzureVirtualMachine {
    private IAzureResourceManager resourceService;

    private AzureResourceManager azureResourceManager;

    private Region region;

    public AzureVirtualMachine(IAzureResourceManager resourceService) {
        this.resourceService = resourceService;
        this.azureResourceManager = resourceService.getManager();
        this.region = resourceService.getRegin();
    }

    public void VirtualMachinelist() throws IOException {
        PagedIterable<VirtualMachineImage> virtualMachineImages = azureResourceManager.virtualMachineImages().listByRegion(Region.ASIA_EAST);
//        PagedIterable<VirtualMachinePublisher> virtualMachinePublishers = azureResourceManager.virtualMachineImages().publishers().listByRegion(Region.ASIA_EAST);
        VirtualMachinePublisher chosenPublisher;
        PrintWriter writer = new PrintWriter(new FileWriter("C:\\Users\\liyah\\Desktop\\output2.txt", true));
        virtualMachineImages.forEach(virtualMachineImage -> {
            writer.println("Image - " + virtualMachineImage.publisherName() + "/"
                    + virtualMachineImage.offer() + "/"
                    + virtualMachineImage.sku());
        });
        writer.close();
        System.out.println("end------------------------------------------------");

    }

    public List<Map<String,String>> listsAllAvailableResourceSKUsForTheSpecifiedRegion() throws IOException {
        ArrayList<Map<String,String>> dataMaps = new ArrayList<>();
        PagedIterable<ResourceSkuInner> list = azureResourceManager
                .virtualMachines()
                .manager()
                .serviceClient()
                .getResourceSkus()
                .list();

        // 使用 Set 来存储已经出现过的 sku.name()，保证不重复
        Set<String> uniqueNames = new HashSet<>();
        for (ResourceSkuInner sku : list) {
            if (ComputeResourceType.VIRTUALMACHINES.toString().equals(sku.resourceType())) {
                String name = sku.name();
                List<String> locations = sku.locations();
                if (!uniqueNames.contains(sku.name()) && (locations.contains(Region.ASIA_EAST.label())||locations.contains(Region.ASIA_EAST.name()))) {
                    // 如果当前 sku.name() 没有出现过，则添加到 uniqueNames 集合中，并保留这个 sku 作为第一个唯一的 sku
                    uniqueNames.add(sku.name());
                    // 将 capabilities 转换为一个 Map，并直接添加到 dataMap 中
                    Map<String, String> capabilityMap = sku.capabilities().stream()
                            .collect(Collectors.toMap(ResourceSkuCapabilities::name, ResourceSkuCapabilities::value));
                    // 添加 name 和 capabilityMap 到 dataMap
                    capabilityMap.put("name",name);
                    dataMaps.add(capabilityMap);
                }
            }
        }
        return dataMaps;
    }

    public void create(CloudScenePluginHandler pluginHandler, CloudPluginSceneElementBO cloudElement, List<VirtualNic> virtualNics, String userData) {
        String groupName = pluginHandler.getSourceSceneId();

        // azure todo 调用微软云api 查询或者创建一个 flavor
        VirtualMachineSizeTypes flavor = StringUtils.isNotBlank(cloudElement.getVmSizeType()) ? VirtualMachineSizeTypes.fromString(cloudElement.getVmSizeType()) : VirtualMachineSizeTypes.STANDARD_B1S;

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
//        设置登录信息
        String userNmae = null;
        String password = null;
        for (VirtualNic.AccessMode accessMode : virtualNic.getAccessModes()) {
            if (StringUtils.isNotBlank(accessMode.getUsername()) && StringUtils.isNotBlank(accessMode.getPassword())) {
                userNmae = accessMode.getUsername();
                password = accessMode.getPassword();
            }
        }
        String netId = virtualNics.stream()
                .filter(v -> StringUtils.isNotBlank(v.getNetworkId()))
                .map(v -> v.getNetworkId()).findFirst()
                .get();
        VirtualMachine.DefinitionStages.WithCreate creater;
        if (BaseSceneElementModel.SystemType.WINDOWS.equals(cloudElement.getVmType())) {
            creater = resourceService.getManager().virtualMachines()
                    .define(vmName)
                    .withRegion(region)
                    .withNewResourceGroup(groupName)
                    .withExistingPrimaryNetwork(azureResourceManager.networks().getById(netId))
                    .withSubnet(IpUtils.extractNetworkPart(virtualNic.getFixedIp()))
                    .withPrimaryPrivateIPAddressStatic(virtualNic.getFixedIp())
                    .withoutPrimaryPublicIPAddress()
                    .withLatestWindowsImage(cloudElement.getPublisher(), cloudElement.getOffer(), cloudElement.getSku())
                    .withAdminUsername(StringUtils.isBlank(userNmae) ? "azureuser" : userNmae)
                    .withAdminPassword(StringUtils.isBlank(password) ? "Azure12345678" : password)
                    .withUnmanagedDisks()
                    .withCustomData(Base64.getEncoder().encodeToString(userData.getBytes()))
                    .defineUnmanagedDataDisk("disk-1")
                    .withNewVhd(cloudElement.getSrcElement().getDisk())
                    .withLun(1)
                    .attach()
                    .withSize(flavor);

        } else {
            creater = resourceService.getManager().virtualMachines()
                    .define(vmName)
                    .withRegion(region)
                    .withNewResourceGroup(groupName)
                    .withExistingPrimaryNetwork(azureResourceManager.networks().getById(netId))
                    .withSubnet(IpUtils.extractNetworkPart(virtualNic.getFixedIp()))
                    .withPrimaryPrivateIPAddressStatic(virtualNic.getFixedIp())
                    .withoutPrimaryPublicIPAddress()
                    .withLatestLinuxImage(cloudElement.getPublisher(), cloudElement.getOffer(), cloudElement.getSku())
                    .withRootUsername(StringUtils.isBlank(userNmae) ? "root1" : userNmae)
                    .withRootPassword(StringUtils.isBlank(password) ? "ycxx123#" : password) // 必须要是6位以上,且有一个特殊字符
                    .withUnmanagedDisks()
                    .withCustomData(Base64.getEncoder().encodeToString(userData.getBytes()))
                    .defineUnmanagedDataDisk("disk-1")
                    .withNewVhd(cloudElement.getSrcElement().getDisk())
                    .withLun(1)
                    .attach()
                    .withSize(flavor);
        }


        // 加其他网卡
        for (int i = 1; i < virtualNics.size(); i++) {
            VirtualNic nic = virtualNics.get(i);
            Network azureNetwork = azureResourceManager.networks().getById(netId);
            NetworkInterface ni = azureResourceManager.networkInterfaces()
                    .define("nic" + nic.getFixedIp())
                    .withRegion(region)
                    .withNewResourceGroup(groupName)
                    .withExistingPrimaryNetwork(azureNetwork)
                    .withSubnet(IpUtils.extractNetworkPart(nic.getFixedIp()))
                    .withPrimaryPrivateIPAddressStatic(nic.getFixedIp())
                    .create();
            creater.withExistingSecondaryNetworkInterface(ni);
        }

        // azure todo 创建虚拟机 回写数据
        VirtualMachine vm = creater.create();
        cloudElement.setResourceId(vm.id());
    }

    public RunCommandResult runCommand(CloudPluginSceneElementBO cloudElement, List<String> scriptContentList) {
//        TODO 已创建的虚拟机只能输入命令 不能直接运行脚本 1.所有安装脚本在启动时放入虚拟机（无后新增探针脚本） 2.探针脚本放文件服务 安装时先下载探针脚本后命令启动
        VirtualMachine virtualMachine = azureResourceManager.virtualMachines().getById(cloudElement.getResourceId());
        String createFileCommand = "echo '你好' > /home/root22/hello22.txt";
//        linux:RunShellScript windows:RunPowerShellScript
        RunCommandInput inputCommand = new RunCommandInput()
                .withCommandId("RunShellScript")
                .withScript(scriptContentList);
        return virtualMachine.runCommand(inputCommand);
    }

    public void delete(CloudPluginSceneElementBO cloudElement) {
        VirtualMachine virtualMachine = azureResourceManager.virtualMachines().getById(cloudElement.getResourceId());
        virtualMachine.deallocate();//停机，释放资源
//        删除虚拟机
        azureResourceManager.virtualMachines().deleteById(cloudElement.getResourceId());
//        删除存储账户 无法从virtualMachine直接找到存储账户name stg472874959defb
//        virtualMachine.storageProfile().osDisk().vhd().uri() https://stg472874959defb.blob.core.windows.net/vhds/MP_SCENE_ac5a089e081147e6af2406942cdd44bf-os-disk-b6e312ca-9d10-485a-a9f7-136bee35135a.vhd
        String uri = virtualMachine.storageProfile().osDisk().vhd().uri();
        String storageName = extractStorageAccountNameFromVhdUri(uri);
        String groupName = virtualMachine.resourceGroupName();
        azureResourceManager.storageAccounts().deleteByResourceGroup(groupName, storageName);
//        删除网络接口
        azureResourceManager.networkInterfaces().deleteById(virtualMachine.getPrimaryNetworkInterface().id());
    }

    // 从 VHD URI 中提取存储账户 ID
    private static String extractStorageAccountNameFromVhdUri(String vhdUri) {
        // 假设 VHD URI 的格式为：https://<storage-account-name>.blob.core.windows.net/<container-name>/<vhd-name>.vhd
        // 根据该格式，可以提取存储账户
        String[] parts = vhdUri.split("/");
        if (parts.length >= 4) {
            return parts[2].substring(0, parts[2].indexOf('.'));
        }
        return null;
    }

    public void execCommand(String group, String vmName, List<String> commands) {
        azureResourceManager.virtualMachines().runShellScript(group, vmName, commands, new ArrayList<>());
    }

    public void execCoand(String group, String vmName, List<String> commands) {
        azureResourceManager.virtualMachines().runShellScript(group, vmName, commands, new ArrayList<>());
    }

    public void pause(String instanceId) {
        VirtualMachine virtualMachine = azureResourceManager.virtualMachines().getById(instanceId);
        virtualMachine.powerOff();
    }

    public void restart(String instanceId) {
        VirtualMachine virtualMachine = azureResourceManager.virtualMachines().getById(instanceId);
        virtualMachine.restart();
    }

    public void start(String instanceId) {
        VirtualMachine virtualMachine = azureResourceManager.virtualMachines().getById(instanceId);
        virtualMachine.start();
    }

    public void unpause(String instanceId) {
        VirtualMachine virtualMachine = azureResourceManager.virtualMachines().getById(instanceId);
        virtualMachine.start();
    }

    public void stop(String instanceId) {
        VirtualMachine virtualMachine = azureResourceManager.virtualMachines().getById(instanceId);
        virtualMachine.deallocate();
    }
}
