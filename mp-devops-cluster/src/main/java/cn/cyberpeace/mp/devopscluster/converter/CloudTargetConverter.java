package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetCalculate;
import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetContainer;
import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetNet;
import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetPO;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CloudTargetConverter {

    public void cloudNetServiceConvert(String cloudType, List<DeviceCloudTargetPO> list, List<DeviceCloudTargetNet> netList, List getCloudInternetServices) {
        //网络服务
        for(int i = 0; i < getCloudInternetServices.size(); i++){
            //主表元素
            DeviceCloudTargetPO deviceCloudTarget = new DeviceCloudTargetPO();
            String id = String.valueOf(UUID.randomUUID());
            deviceCloudTarget.setId(id);
            deviceCloudTarget.setName(((JSONObject) getCloudInternetServices.get(i)).get("netServiceName") != null ? ((JSONObject) getCloudInternetServices.get(i)).get("netServiceName").toString() : "");
            deviceCloudTarget.setOtherName(((JSONObject) getCloudInternetServices.get(i)).get("netServiceName") != null ? ((JSONObject) getCloudInternetServices.get(i)).get("netServiceName").toString() : "");
            deviceCloudTarget.setCloudType(cloudType);
            deviceCloudTarget.setCloudServiceType(1);
            deviceCloudTarget.setOperatingSystemStatus("专业化");
            deviceCloudTarget.setGeneration("第一代");
            deviceCloudTarget.setVersion("0.0.1");
            list.add(deviceCloudTarget);

            //网络副表元素
            DeviceCloudTargetNet deviceCloudTargetNet = new DeviceCloudTargetNet();
            deviceCloudTargetNet.setLinkId(id);
            deviceCloudTargetNet.setNetServiceName(((JSONObject) getCloudInternetServices.get(i)).get("netServiceName") != null ? ((JSONObject) getCloudInternetServices.get(i)).get("netServiceName").toString() : "");
            deviceCloudTargetNet.setSourceType(1);
            deviceCloudTargetNet.setCloudType(cloudType);
            deviceCloudTargetNet.setNetType(((JSONObject) getCloudInternetServices.get(i)).get("netType") != null ? ((JSONObject) getCloudInternetServices.get(i)).get("netType").toString() : "");
            deviceCloudTargetNet.setIsPublic(2);
            deviceCloudTargetNet.setQuickCreateSupport(1);
            deviceCloudTargetNet.setIsImport(0);
            deviceCloudTargetNet.setIsHide(0);
            deviceCloudTargetNet.setIsPublic(2);
            deviceCloudTargetNet.setIsPublic(2);
            deviceCloudTargetNet.setLogo("/api/mp/file/710/017/93dde93031be465c3a7bb20381f4cd6e.png");
            netList.add(deviceCloudTargetNet);
        }
    }

    public void cloudContainerRegistryConvert(String cloudType, List<DeviceCloudTargetPO> list, List<DeviceCloudTargetContainer> containerList, List dockerImages) {
        //docker镜像
        for (int i = 0; i < dockerImages.size(); i++) {
            //主表元素
            DeviceCloudTargetPO deviceCloudTarget = new DeviceCloudTargetPO();
            deviceCloudTarget.setName(((JSONObject) dockerImages.get(i)).get("repositoryName") != null ? ((JSONObject) dockerImages.get(i)).get("repositoryName").toString() : "");
            deviceCloudTarget.setOtherName(((JSONObject) dockerImages.get(i)).get("repositoryName") != null ? ((JSONObject) dockerImages.get(i)).get("repositoryName").toString() : "");
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
            deviceCloudTargetContainer.setName(((JSONObject) dockerImages.get(i)).get("repositoryName") != null ? ((JSONObject) dockerImages.get(i)).get("repositoryName").toString() : "");
            deviceCloudTargetContainer.setSourceType(2);
            deviceCloudTargetContainer.setCloudType(cloudType);
            deviceCloudTargetContainer.setContainerType("containerInfrastructure");
            deviceCloudTargetContainer.setSubType("containerRegistry");
            deviceCloudTargetContainer.setUploadTargetName(((JSONObject) dockerImages.get(i)).get("repositoryName") != null ? ((JSONObject) dockerImages.get(i)).get("repositoryName").toString() : "");
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

    public void cloudCalculateSoftVMConvert(String cloudType, List<DeviceCloudTargetPO> list, List<DeviceCloudTargetCalculate> calculateList, List images) {
        //微软云镜像
        for (int i = 0; i < images.size(); i++) {
            //主表元素
            DeviceCloudTargetPO deviceCloudTarget = new DeviceCloudTargetPO();
            deviceCloudTarget.setName(((JSONObject) images.get(i)).get("imageName") != null ? ((JSONObject) images.get(i)).get("imageName").toString() : "");
            deviceCloudTarget.setOtherName(((JSONObject) images.get(i)).get("imageName") != null ? ((JSONObject) images.get(i)).get("imageName").toString() : "");
            String id = String.valueOf(UUID.randomUUID());
            deviceCloudTarget.setId(id);
            //云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储
            deviceCloudTarget.setCloudServiceType(2);
            deviceCloudTarget.setCloudType(cloudType);
            deviceCloudTarget.setOperatingSystem(((JSONObject) images.get(i)).get("operatingSystem") != null ? ((JSONObject) images.get(i)).get("operatingSystem").toString() : "");
            deviceCloudTarget.setOperatingSystemStatus("专业化");
            deviceCloudTarget.setGeneration("第一代");
            deviceCloudTarget.setVersion(((JSONObject) images.get(i)).get("imageVersion") != null ? ((JSONObject) images.get(i)).get("imageVersion").toString() : "");
            list.add(deviceCloudTarget);

            //计算副表元素
            DeviceCloudTargetCalculate deviceCloudTargetCalculate = new DeviceCloudTargetCalculate();
            deviceCloudTargetCalculate.setLinkId(id);
            deviceCloudTargetCalculate.setName(((JSONObject) images.get(i)).get("imageName") != null ? ((JSONObject) images.get(i)).get("imageName").toString() : "");
            deviceCloudTargetCalculate.setSourceType(1);
            deviceCloudTargetCalculate.setCloudType(cloudType);
            deviceCloudTargetCalculate.setPublisher(((JSONObject) images.get(i)).get("PublisherName") != null ? ((JSONObject) images.get(i)).get("PublisherName").toString() : "");
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
            deviceCloudTargetCalculate.setLogo("/api/mp/file/710/017/93dde93031be465c3a7bb20381f4cd6e.png");
            deviceCloudTargetCalculate.setGeneration("第一代");
            deviceCloudTargetCalculate.setVersion(((JSONObject) images.get(i)).get("imageVersion") != null ? ((JSONObject) images.get(i)).get("imageVersion").toString() : "");
            calculateList.add(deviceCloudTargetCalculate);
        }
    }

    public void cloudCalculateUploadVMConvert(String cloudType, List<DeviceCloudTargetPO> list, List<DeviceCloudTargetCalculate> calculateList, List localImages) {
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
            deviceCloudTargetCalculate.setCloudType(cloudType);
            deviceCloudTargetCalculate.setPublisher(((JSONObject) localImages.get(i)).get("PublisherName") != null ? ((JSONObject) localImages.get(i)).get("PublisherName").toString() : "");
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
            deviceCloudTargetCalculate.setLogo("/api/mp/file/710/017/93dde93031be465c3a7bb20381f4cd6e.png");
            deviceCloudTargetCalculate.setGeneration("第一代");
            deviceCloudTargetCalculate.setVersion(((JSONObject) localImages.get(i)).get("imageVersion") != null ? ((JSONObject) localImages.get(i)).get("imageVersion").toString() : "");
            calculateList.add(deviceCloudTargetCalculate);
        }
    }

    public void cloudContainerInstanceConvert(String cloudType, List<DeviceCloudTargetPO> mainTargetList, List<DeviceCloudTargetContainer> containerList, List dockerInstances) {
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
