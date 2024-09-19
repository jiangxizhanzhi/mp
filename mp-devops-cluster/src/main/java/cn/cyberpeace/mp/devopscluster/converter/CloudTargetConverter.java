package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devopscluster.po.*;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CloudTargetConverter {

    public void convertExtNet(CloudDevice device, DeviceCloudTargetVO target) {
        /**
         * ext_net
         */
        device.setRole(24);
        device.setCategory(3);
        device.setImageType("VNet");
        ExtNet extNet = new ExtNet();
        List<AddressPool> addressPools = new ArrayList<>();
        AddressPool addressPool = new AddressPool();
        addressPools.add(addressPool);
        extNet.setAddressPools(addressPools);
        device.setExtNet(extNet);

        /**
         * ext_system_info
         */
        ExtSystemInfo extSystemInfo = new ExtSystemInfo();
        extSystemInfo.setLanguage("cn");
        extSystemInfo.setSystemArchitecture("64");
        device.setExtSystemInfo(extSystemInfo);

        /**
         * ext_image_info
         */
        ExtImageInfo extImageInfo = new ExtImageInfo();
        device.setExtImageInfo(extImageInfo);

        /**
         * device_cloud_target
         */
        device.setDeviceCloudTarget(target);
    }

    public void convertExtNetFW(CloudDevice device, DeviceCloudTargetVO target) {
        /**
         * ext_net
         */
        device.setRole(21);
        device.setCategory(4);
        device.setImageType("fireWall");
        ExtFireWall extFireWall = new ExtFireWall();
        extFireWall.setIsPublicIp(true);
        //防火墙策略
        FirewallPolicy firewallPolicy = new FirewallPolicy();
        List<FirewallPolicyRule> rules = new ArrayList<>();
        rules.add(new FirewallPolicyRule());
        firewallPolicy.setRules(rules);
        extFireWall.setFirewallPolicy(firewallPolicy);
        device.setExtFireWall(extFireWall);

        /**
         * ext_system_info
         */
        ExtSystemInfo extSystemInfo = new ExtSystemInfo();
        extSystemInfo.setLanguage("cn");
        extSystemInfo.setSystemArchitecture("64");
        device.setExtSystemInfo(extSystemInfo);

        /**
         * ext_image_info
         */
        ExtImageInfo extImageInfo = new ExtImageInfo();
        device.setExtImageInfo(extImageInfo);

        /**
         * device_cloud_target
         */
        device.setDeviceCloudTarget(target);
    }

    public void convertExtNetWAF(CloudDevice device, DeviceCloudTargetVO target) {
        /**
         * ext_net
         */
        device.setRole(21);
        device.setCategory(4);
        device.setImageType("WAF");

        /**
         * device_cloud_target
         */
        device.setDeviceCloudTarget(target);
    }

    public void convertExtVm(CloudDevice device, DeviceCloudTargetVO target) {
        String operatingSystem = target.getOperatingSystem();
        /**
         * ext_vm
         */
        device.setImageType("laasVm");
        device.setCategory(1);
        device.setRole(4);
        ExtVm extVm = new ExtVm();
        extVm.setFlavor("1CPU/1G内存/30G磁盘/2网卡");
        extVm.setCpu(1);
        extVm.setRam(1);
        if (operatingSystem!= null && "Linux".equals(operatingSystem)) {
            extVm.setFlavor("1CPU/1G内存/30G磁盘/2网卡");
            extVm.setCpu(1);
            extVm.setRam(1);
            extVm.setDisk(30);
        } else {
            extVm.setFlavor("1CPU/1G内存/127G磁盘/2网卡");
            extVm.setCpu(1);
            extVm.setRam(1);
            extVm.setDisk(127);
        }
        extVm.setVmSizeType("Standard_B1s");
        extVm.setVmOsDiskType("StandardSSD_LRS");
        extVm.setInitSupport(true);
        extVm.setQuickCreateSupport(false);
        extVm.setUsername("admin123");
        extVm.setPassword("NHiOJiZTD4K1");
        extVm.setVmType(target.getOperatingSystem());
        extVm.setExtInfo(null);
        extVm.setInstanceExist(false);
        extVm.setSnapshotCount(0);//快照计数
        extVm.setInstanceState(0);
        extVm.setIsoAttach(false);

        //访问协议
        List<AccessProtocol> accessProtocols = new ArrayList<>();
        AccessProtocol accessProtocol = new AccessProtocol();
        //ip地址
        accessProtocol.setHostname("");
        if (operatingSystem!= null && "Linux".equals(operatingSystem)) {
            accessProtocol.setProtocol("SSH");
            extVm.setProtocols("SSH");
        } else if (operatingSystem!= null && "Windows".equals(operatingSystem)) {
            accessProtocol.setProtocol("RDP");
            extVm.setProtocols("RDP");
        } else {
            accessProtocol.setProtocol("SSH");
            extVm.setProtocols("SSH");
        }
        accessProtocols.add(accessProtocol);
        extVm.setAccessProtocols(accessProtocols);
        extVm.setIsPublicIp(true);
        extVm.setPublicIpAddress("");

        device.setExtVm(extVm);

        /**
         * ext_system_info
         */
        ExtSystemInfo extSystemInfo = new ExtSystemInfo();
        extSystemInfo.setLanguage("cn");
        extSystemInfo.setSystemArchitecture("64");
        device.setExtSystemInfo(extSystemInfo);

        /**
         * ext_image_info
         */
        ExtImageInfo extImageInfo = new ExtImageInfo();
        //云靶标只能拿到linux和windows两类
        if (operatingSystem!= null && "Linux".equals(operatingSystem)) {
            extSystemInfo.setSystemType(306);
        } else if (operatingSystem!= null && "Windows".equals(operatingSystem)) {
            extSystemInfo.setSystemType(106);
        }
        device.setExtImageInfo(extImageInfo);

        /**
         * device_cloud_target
         */
        device.setDeviceCloudTarget(target);
    }

    public void convertExtDocker(CloudDevice device, DeviceCloudTargetVO target) {
        /**
         * ext_docker
         */
        device.setImageType("containerRegistry");
        device.setCategory(2);
        device.setRole(4);
        ExtDocker extDocker = new ExtDocker();
        extDocker.setFlavor("1vcpu/1GiB内存");
        extDocker.setCpu(1);
        extDocker.setRam(1);
        extDocker.setInstanceState(0);
        extDocker.setInitSupport(true);
        extDocker.setQuickCreateSupport(true);
        extDocker.setSnapshotCount(0);
        extDocker.setExtInfo(new Snapshot.ExtInfo());
        extDocker.setIsPublicIp(true);
        extDocker.setPublicIpAddress("");
        extDocker.setProtocols("SSH");
        extDocker.setVmType(target.getOperatingSystem());

        //访问协议
        List<AccessProtocol> accessProtocols = new ArrayList<>();
        AccessProtocol accessProtocol = new AccessProtocol();
        accessProtocol.setHostname("");
        accessProtocol.setProtocol("SSH");
        accessProtocols.add(accessProtocol);
        extDocker.setAccessProtocols(accessProtocols);

        device.setExtDocker(extDocker);

        /**
         * ext_system_info
         */
        ExtSystemInfo extSystemInfo = new ExtSystemInfo();
        extSystemInfo.setLanguage("en");
        //云靶标只能拿到linux和windows两类
        String operatingSystem = target.getOperatingSystem();
        if (operatingSystem!= null && "Linux".equals(operatingSystem)) {
            extSystemInfo.setSystemType(306);
        } else if (operatingSystem!= null && "Windows".equals(operatingSystem)) {
            extSystemInfo.setSystemType(106);
        }
        extSystemInfo.setSystemArchitecture("64");
        device.setExtSystemInfo(extSystemInfo);

        /**
         * ext_image_info
         */
        ExtImageInfo extImageInfo = new ExtImageInfo();
        device.setExtImageInfo(extImageInfo);

        /**
         * device_cloud_target
         */
        device.setDeviceCloudTarget(target);
    }


    public void cloudNetServiceConvert(String cloudType, List<DeviceCloudTargetPO> list, List<DeviceCloudTargetNet> netList, List getCloudInternetServices) {
        if (getCloudInternetServices != null && getCloudInternetServices.size() > 0) {
            //网络服务
            for (int i = 0; i < getCloudInternetServices.size(); i++) {
                //主表元素
                DeviceCloudTargetPO deviceCloudTarget = new DeviceCloudTargetPO();
                String id = String.valueOf(UUID.randomUUID());
                deviceCloudTarget.setId(id);
                deviceCloudTarget.setName(((JSONObject) getCloudInternetServices.get(i)).get("name") != null ? ((JSONObject) getCloudInternetServices.get(i)).get("name").toString() : "");
                deviceCloudTarget.setOtherName(((JSONObject) getCloudInternetServices.get(i)).get("name") != null ? ((JSONObject) getCloudInternetServices.get(i)).get("name").toString() : "");
                deviceCloudTarget.setCloudType(cloudType);
                deviceCloudTarget.setCloudServiceType(1);
                deviceCloudTarget.setOperatingSystemStatus("专业化");
                deviceCloudTarget.setGeneration("第一代");
                deviceCloudTarget.setVersion("0.0.1");
                list.add(deviceCloudTarget);

                //网络副表元素
                DeviceCloudTargetNet deviceCloudTargetNet = new DeviceCloudTargetNet();
                deviceCloudTargetNet.setLinkId(id);
                deviceCloudTargetNet.setName(((JSONObject) getCloudInternetServices.get(i)).get("name") != null ? ((JSONObject) getCloudInternetServices.get(i)).get("name").toString() : "");
                deviceCloudTargetNet.setSourceType(1);
                deviceCloudTargetNet.setCloudType(cloudType);
                deviceCloudTargetNet.setNetType(((JSONObject) getCloudInternetServices.get(i)).get("netType") != null ? ((JSONObject) getCloudInternetServices.get(i)).get("netType").toString() : "");
                deviceCloudTargetNet.setSubType(((JSONObject) getCloudInternetServices.get(i)).get("subType") != null ? ((JSONObject) getCloudInternetServices.get(i)).get("subType").toString() : "");
                deviceCloudTargetNet.setIsPublic(2);
                deviceCloudTargetNet.setQuickCreateSupport(1);
                deviceCloudTargetNet.setIsImport(0);
                deviceCloudTargetNet.setIsHide(0);
                deviceCloudTargetNet.setIsPublic(2);
                deviceCloudTargetNet.setIsPublic(2);
                String subType = ((JSONObject) getCloudInternetServices.get(i)).get("subType") != null ? ((JSONObject) getCloudInternetServices.get(i)).get("subType").toString() : "";
                if(subType.equals("fireWall")){
                    deviceCloudTargetNet.setLogo("/api/mp/file/229/718/ff193b62dc805ee24838086d083fd7fe.png");
                }else if(subType.equals("VNet")){
                    deviceCloudTargetNet.setLogo("/api/mp/file/177/108/39ae20d4c7de4ab7a9aa9de270c1d143.png");
                }else if(subType.equals("WAF")){
                    deviceCloudTargetNet.setLogo("/api/mp/file/229/718/ff193b62dc805ee24838086d083fd7fe.png");
                }else{
                    deviceCloudTargetNet.setLogo("/api/mp/file/710/017/93dde93031be465c3a7bb20381f4cd6e.png");
                }
                netList.add(deviceCloudTargetNet);
            }
        }
    }

    public void cloudContainerRegistryConvert(String cloudType, List<DeviceCloudTargetPO> list, List<DeviceCloudTargetContainer> containerList, List<JSONObject> dockerImages) {
        if (dockerImages != null && dockerImages.size() > 0) {
            //docker镜像
            for (int i = 0; i < dockerImages.size(); i++) {
                //主表元素
                DeviceCloudTargetPO deviceCloudTarget = new DeviceCloudTargetPO();
                String serverUrl = dockerImages.get(i).get("serverUrl") != null ? dockerImages.get(i).get("serverUrl").toString() : "";
                String repositoryName =  dockerImages.get(i).get("repositoryName") != null ?  dockerImages.get(i).get("repositoryName").toString() : "";
                String tag =  dockerImages.get(i).get("tag") != null ? dockerImages.get(i).get("tag").toString() : "";
                deviceCloudTarget.setName(repositoryName + ":" + tag);
                deviceCloudTarget.setOtherName(repositoryName + ":" + tag);
                String id = String.valueOf(UUID.randomUUID());
                deviceCloudTarget.setId(id);
                deviceCloudTarget.setCloudType(cloudType);
                //云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储
                deviceCloudTarget.setCloudServiceType(3);
                deviceCloudTarget.setOperatingSystem("Linux");
                deviceCloudTarget.setOperatingSystemStatus("专业化");
                deviceCloudTarget.setGeneration("第一代");
                deviceCloudTarget.setVersion("0.0.1");
                list.add(deviceCloudTarget);

                //容器副表元素
                DeviceCloudTargetContainer deviceCloudTargetContainer = new DeviceCloudTargetContainer();
                deviceCloudTargetContainer.setLinkId(id);
                deviceCloudTargetContainer.setName(repositoryName + ":" + tag);
                deviceCloudTargetContainer.setSourceType(2);
                deviceCloudTargetContainer.setCloudType(cloudType);
                deviceCloudTargetContainer.setContainerType("containerInfrastructure");
                deviceCloudTargetContainer.setSubType("containerRegistry");
                deviceCloudTargetContainer.setUploadTargetName(serverUrl + "/" + repositoryName + ":" + tag);
                deviceCloudTargetContainer.setOperatingSystem("Linux");
                deviceCloudTargetContainer.setOperatingSystemStatus("专业化");
                deviceCloudTargetContainer.setIsPublic(2);
                deviceCloudTargetContainer.setQuickCreateSupport(1);
                deviceCloudTargetContainer.setIsImport(0);
                deviceCloudTargetContainer.setIsHide(0);
                deviceCloudTargetContainer.setIsPublic(2);
                deviceCloudTargetContainer.setIsPublic(2);
                deviceCloudTargetContainer.setLogo("/api/mp/file/710/017/93dde93031be465c3a7bb20381f4cd6e.png");
                deviceCloudTargetContainer.setGeneration("第一代");
                deviceCloudTargetContainer.setVersion("0.0.1");
                containerList.add(deviceCloudTargetContainer);
            }
        }
    }

    public void cloudCalculateSoftVMConvert(String cloudType, List<DeviceCloudTargetPO> list, List<DeviceCloudTargetCalculate> calculateList, List images) {
        if (images != null && images.size() > 0) {
            //微软云镜像
            for (int i = 0; i < images.size(); i++) {
                //主表元素
                DeviceCloudTargetPO deviceCloudTarget = new DeviceCloudTargetPO();
                String imageName = ((JSONObject) images.get(i)).get("imageName") != null ? ((JSONObject) images.get(i)).get("imageName").toString() : "";
                String imageVersion = ((JSONObject) images.get(i)).get("imageVersion") != null ? ((JSONObject) images.get(i)).get("imageVersion").toString() : "";
                deviceCloudTarget.setName(imageName + "_" + imageVersion);
                deviceCloudTarget.setOtherName(imageName + "_" + imageVersion);
                String id = String.valueOf(UUID.randomUUID());
                deviceCloudTarget.setId(id);
                //云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储
                deviceCloudTarget.setCloudServiceType(2);
                deviceCloudTarget.setCloudType(cloudType);
                deviceCloudTarget.setOperatingSystem(((JSONObject) images.get(i)).get("operatingSystem") != null ? ((JSONObject) images.get(i)).get("operatingSystem").toString() : "");
                deviceCloudTarget.setOperatingSystemStatus("专业化");
                deviceCloudTarget.setGeneration("第一代");
                deviceCloudTarget.setVersion(imageVersion);
                list.add(deviceCloudTarget);

                //计算副表元素
                DeviceCloudTargetCalculate deviceCloudTargetCalculate = new DeviceCloudTargetCalculate();
                deviceCloudTargetCalculate.setLinkId(id);
                deviceCloudTargetCalculate.setName(imageName + "_" + imageVersion);
                deviceCloudTargetCalculate.setSourceType(1);
                deviceCloudTargetCalculate.setCloudType(cloudType);
                deviceCloudTargetCalculate.setPublisher(((JSONObject) images.get(i)).get("publisherName") != null ? ((JSONObject) images.get(i)).get("publisherName").toString() : "");
                deviceCloudTargetCalculate.setOffer(((JSONObject) images.get(i)).get("offerName") != null ? ((JSONObject) images.get(i)).get("offerName").toString() : "");
                deviceCloudTargetCalculate.setSku(((JSONObject) images.get(i)).get("skuName") != null ? ((JSONObject) images.get(i)).get("skuName").toString() : "");
                deviceCloudTargetCalculate.setOperatingSystem(((JSONObject) images.get(i)).get("operatingSystem") != null ? ((JSONObject) images.get(i)).get("operatingSystem").toString() : "");
                deviceCloudTargetCalculate.setOperatingSystemStatus("专业化");
                deviceCloudTargetCalculate.setCalculateType("laas");
                deviceCloudTargetCalculate.setSubType("vm");
                deviceCloudTargetCalculate.setIsPublic(2);
                deviceCloudTargetCalculate.setQuickCreateSupport(1);
                deviceCloudTargetCalculate.setIsImport(0);
                deviceCloudTargetCalculate.setIsHide(0);
                deviceCloudTargetCalculate.setIsPublic(2);
                deviceCloudTargetCalculate.setIsPublic(2);
                deviceCloudTargetCalculate.setLogo("/api/mp/file/247/911/4691e31013c189bd4c63360326497e25.png");
                deviceCloudTargetCalculate.setGeneration("第一代");
                deviceCloudTargetCalculate.setVersion(imageVersion);
                calculateList.add(deviceCloudTargetCalculate);
            }
        }
    }

    public void cloudCalculateUploadVMConvert(String cloudType, List<DeviceCloudTargetPO> list, List<DeviceCloudTargetCalculate> calculateList, List localImages) {
        if (localImages != null && localImages.size() > 0) {
            //微软云本地镜像
            for (int i = 0; i < localImages.size(); i++) {
                //主表元素
                DeviceCloudTargetPO deviceCloudTarget = new DeviceCloudTargetPO();
                deviceCloudTarget.setName(((JSONObject) localImages.get(i)).get("imageName") != null ? ((JSONObject) localImages.get(i)).get("imageName").toString() : "");
                deviceCloudTarget.setOtherName(((JSONObject) localImages.get(i)).get("imageName") != null ? ((JSONObject) localImages.get(i)).get("imageName").toString() : "");
                String id = String.valueOf(UUID.randomUUID());
                deviceCloudTarget.setId(id);
                //云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储
                deviceCloudTarget.setCloudServiceType(2);
                deviceCloudTarget.setCloudType(cloudType);
                deviceCloudTarget.setOperatingSystem(((JSONObject) localImages.get(i)).get("operatingSystem") != null ? ((JSONObject) localImages.get(i)).get("operatingSystem").toString() : "");
                deviceCloudTarget.setOperatingSystemStatus("专业化");
                deviceCloudTarget.setGeneration("第一代");
                deviceCloudTarget.setVersion(((JSONObject) localImages.get(i)).get("imageVersion") != null ? ((JSONObject) localImages.get(i)).get("imageVersion").toString() : "");

                list.add(deviceCloudTarget);

                //计算副表元素
                DeviceCloudTargetCalculate deviceCloudTargetCalculate = new DeviceCloudTargetCalculate();
                deviceCloudTargetCalculate.setLinkId(id);
                deviceCloudTargetCalculate.setName(((JSONObject) localImages.get(i)).get("imageName") != null ? ((JSONObject) localImages.get(i)).get("imageName").toString() : "");
                deviceCloudTargetCalculate.setSourceType(2);
                deviceCloudTargetCalculate.setUploadImageId(((JSONObject) localImages.get(i)).get("uploadImageId") != null ? ((JSONObject) localImages.get(i)).get("uploadImageId").toString() : "");
                deviceCloudTargetCalculate.setCloudType(cloudType);
                deviceCloudTargetCalculate.setPublisher(((JSONObject) localImages.get(i)).get("publisherName") != null ? ((JSONObject) localImages.get(i)).get("publisherName").toString() : "");
                deviceCloudTargetCalculate.setOffer(((JSONObject) localImages.get(i)).get("offerName") != null ? ((JSONObject) localImages.get(i)).get("offerName").toString() : "");
                deviceCloudTargetCalculate.setSku(((JSONObject) localImages.get(i)).get("skuName") != null ? ((JSONObject) localImages.get(i)).get("skuName").toString() : "");
                deviceCloudTargetCalculate.setOperatingSystem(((JSONObject) localImages.get(i)).get("operatingSystem") != null ? ((JSONObject) localImages.get(i)).get("operatingSystem").toString() : "");
                deviceCloudTargetCalculate.setOperatingSystemStatus("专业化");
                deviceCloudTargetCalculate.setCalculateType("laas");
                deviceCloudTargetCalculate.setSubType("vm");
                deviceCloudTargetCalculate.setIsPublic(2);
                deviceCloudTargetCalculate.setQuickCreateSupport(1);
                deviceCloudTargetCalculate.setIsImport(0);
                deviceCloudTargetCalculate.setIsHide(0);
                deviceCloudTargetCalculate.setIsPublic(2);
                deviceCloudTargetCalculate.setIsPublic(2);
                deviceCloudTargetCalculate.setLogo("/api/mp/file/726/170/948392075ab1178b725e3068137fcd29.png");
                deviceCloudTargetCalculate.setGeneration("第一代");
                deviceCloudTargetCalculate.setVersion(((JSONObject) localImages.get(i)).get("imageVersion") != null ? ((JSONObject) localImages.get(i)).get("imageVersion").toString() : "");
                calculateList.add(deviceCloudTargetCalculate);
            }
        }
    }

    public void cloudContainerInstanceConvert(String cloudType, List<DeviceCloudTargetPO> mainTargetList, List<DeviceCloudTargetContainer> containerList, List dockerInstances) {
        if (dockerInstances != null && dockerInstances.size() > 0) {
            //docker镜像
            for (int i = 0; i < dockerInstances.size(); i++) {
                //主表元素
                DeviceCloudTargetPO deviceCloudTarget = new DeviceCloudTargetPO();
                deviceCloudTarget.setName(((JSONObject) dockerInstances.get(i)).get("containerName") != null ? ((JSONObject) dockerInstances.get(i)).get("containerName").toString() : "");
                deviceCloudTarget.setOtherName(((JSONObject) dockerInstances.get(i)).get("containerName") != null ? ((JSONObject) dockerInstances.get(i)).get("containerName").toString() : "");
                String id = String.valueOf(UUID.randomUUID());
                deviceCloudTarget.setId(id);
                deviceCloudTarget.setCloudType(cloudType);
                //云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储
                deviceCloudTarget.setCloudServiceType(3);
                deviceCloudTarget.setOperatingSystem("Linux");
                deviceCloudTarget.setOperatingSystemStatus("专业化");
                deviceCloudTarget.setGeneration("第一代");
                deviceCloudTarget.setVersion("0.0.1");
                mainTargetList.add(deviceCloudTarget);

                //容器副表元素
                DeviceCloudTargetContainer deviceCloudTargetContainer = new DeviceCloudTargetContainer();
                deviceCloudTargetContainer.setLinkId(id);
                deviceCloudTargetContainer.setName(((JSONObject) dockerInstances.get(i)).get("containerName") != null ? ((JSONObject) dockerInstances.get(i)).get("containerName").toString() : "");
                deviceCloudTargetContainer.setSourceType(2);
                deviceCloudTargetContainer.setCloudType(cloudType);
                deviceCloudTargetContainer.setContainerType("containerInfrastructure");
                deviceCloudTargetContainer.setSubType("containerInstance");
                deviceCloudTargetContainer.setUploadTargetName(((JSONObject) dockerInstances.get(i)).get("image") != null ? ((JSONObject) dockerInstances.get(i)).get("image").toString() : "");
                deviceCloudTargetContainer.setOperatingSystem("Linux");
                deviceCloudTargetContainer.setOperatingSystemStatus("专业化");
                deviceCloudTargetContainer.setIsPublic(2);
                deviceCloudTargetContainer.setQuickCreateSupport(1);
                deviceCloudTargetContainer.setIsImport(0);
                deviceCloudTargetContainer.setIsHide(0);
                deviceCloudTargetContainer.setIsPublic(2);
                deviceCloudTargetContainer.setIsPublic(2);
                deviceCloudTargetContainer.setLogo("/api/mp/file/710/017/93dde93031be465c3a7bb20381f4cd6e.png");
                deviceCloudTargetContainer.setGeneration("第一代");
                deviceCloudTargetContainer.setVersion("0.0.1");
                containerList.add(deviceCloudTargetContainer);
            }
        }
    }
}
