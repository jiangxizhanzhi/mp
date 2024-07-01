package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.auth.util.ParentCodeEnum;
import cn.cyberpeace.mp.base.bo.MpSysCodeMenu;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.cscene.model.MpDevopsBoundaryNetworkEquipment;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.CloudDevice;
import cn.cyberpeace.mp.devopscluster.po.*;
import cn.cyberpeace.mp.devopscluster.repository.MicrosoftAzureMapper;
import cn.cyberpeace.mp.devopscluster.vo.EquipmentRequestVO;
import cn.cyberpeace.mp.devopscluster.vo.MpSysCodeMenuVO;
import cn.cyberpeace.mp.devopscluster.vo.TargerRequestVO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import com.alibaba.fastjson.JSONObject;
import com.azure.containers.containerregistry.ContainerRegistryClient;
import com.azure.containers.containerregistry.ContainerRegistryClientBuilder;
import com.azure.containers.containerregistry.ContainerRepository;
import com.azure.containers.containerregistry.models.ArtifactManifestProperties;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.Region;
import com.azure.core.management.profile.AzureProfile;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.*;
import com.azure.resourcemanager.containerinstance.ContainerInstanceManager;
import com.azure.resourcemanager.containerinstance.models.Container;
import com.azure.resourcemanager.containerinstance.models.ContainerGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MicrosoftAzureConverter implements BaseConverter {
    @Autowired
    private MicrosoftAzureMapper microsoftAzureMapper;

    @Autowired
    private CloudTargetConverter cloudTargetConverter;

    /**
     * 云镜像管理页面分页查询
     */
    public JSONObject getCloudTargetList(JSONObject requestData) {
        Integer pageNum = requestData.get("pageNum") != null ? requestData.getInteger("pageNum") : 1;
        Integer pageSize = requestData.get("pageSize") != null ? requestData.getInteger("pageSize") : 10;
        requestData.put("offset", (pageNum - 1) * pageSize);
        requestData.put("pageSize", pageSize);
        List<DeviceCloudTargetPO> list = microsoftAzureMapper.getCloudTargetList(requestData);
        Integer total = microsoftAzureMapper.getCloudTargetListTotal(requestData);
        JSONObject result = new JSONObject();
        result.put("source", list);
        result.put("total", total);
        return result;
    }

    private JSONObject getReturnData(String cloudServiceType, String tableOtherName, TargerRequestVO request) {
        List<MpSysCodeMenuVO> codes = request.getCodes();
        List<MpSysCodeMenu> sqls = new ArrayList<>();

        // 获取SQL的拼接条件
        if (codes != null && !codes.isEmpty()) {
            sqls = microsoftAzureMapper.getSqlByCodes(codes);
            processSqls(sqls, codes, tableOtherName);
        } else {
            //防止未传条件
            MpSysCodeMenu code = new MpSysCodeMenu();
            code.setSupplement("and " + tableOtherName + ".pk = ''");
            sqls.add(code);
        }

        // 过滤出非空的Supplement字段
        List<MpSysCodeMenu> filteredList = (sqls != null) ?
                sqls.stream()
                        .filter(s -> s.getSupplement() != null && !s.getSupplement().isEmpty())
                        .collect(Collectors.toList()) :
                Collections.emptyList();

        // 分页信息
        int offset = (request.getPage() != null && request.getPerPage() != null) ? (request.getPage() - 1) * request.getPerPage() : 0;
        int pageSize = (request.getPerPage() != null) ? request.getPerPage() : 100000;

        // 获取数据和总数
        List<DeviceCloudTargetVO> list = microsoftAzureMapper.getCloudTargetListByCodes(
                Integer.parseInt(cloudServiceType), filteredList, offset, pageSize, request.getCloudType()
        );
        Integer totalNum = microsoftAzureMapper.getCloudTargetListByCodesNum(
                Integer.parseInt(cloudServiceType), filteredList, request.getCloudType()
        );

        // 构建返回的JSON对象
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", list);
        jsonObject.put("total", totalNum);
        return jsonObject;
    }

    private void processSqls(List<MpSysCodeMenu> sqls, List<MpSysCodeMenuVO> codes, String tableOtherName) {
        for (MpSysCodeMenu sql : sqls) {
            String supplement = sql.getSupplement();
            if (supplement != null && !supplement.isEmpty()) {
                if (supplement.contains("**")) {
                    for (MpSysCodeMenuVO code : codes) {
                        if (code.getCode() != null && sql.getCode() != null && code.getCode().equals(sql.getCode())) {
                            if (code.getValue() != null && !code.getValue().isEmpty()) {
                                sql.setSupplement("and " + supplement.replace("**", code.getValue()));
                            } else {
                                sql.setSupplement("");
                            }
                        }
                    }
                } else if (supplement.contains(".")) {
                    sql.setSupplement("and " + supplement);
                } else {
                    sql.setSupplement("and " + tableOtherName + "." + supplement);
                }
            }
        }
    }

    public List<JSONObject> getCloudTargetListByCodes(TargerRequestVO request) {
        List<DeviceCloudTargetVO> vos = null;
        //分页时计算总数
        Integer total = 0;
        if (request.getCloudServiceType() == null || request.getCloudServiceType().isEmpty()) {
            return new ArrayList<>();
        }
        switch (request.getCloudServiceType()) {
            case "1":
                JSONObject net = getReturnData(request.getCloudServiceType(), "net", request);
                vos = (List<DeviceCloudTargetVO>) net.get("data");
                total = (Integer) net.get("total");
                break;
            case "2":
                JSONObject calculate = getReturnData(request.getCloudServiceType(), "calculate", request);
                vos = (List<DeviceCloudTargetVO>) calculate.get("data");
                total = (Integer) calculate.get("total");
                break;
            case "3":
                JSONObject container = getReturnData(request.getCloudServiceType(), "container", request);
                vos = (List<DeviceCloudTargetVO>) container.get("data");
                total = (Integer) container.get("total");
                break;
            case "4":
                break;
            case "5":
                break;
            case "6":
                break;
            case "7":
                break;
            default:
        }
        //结果转Device集合
        List<CloudDevice> convert = convert(vos);

        //根据convert中subType分类收集成returnDate,list在收集returnDate
        List<JSONObject> list = new ArrayList<>();
        JSONObject returnDate = new JSONObject();
        returnDate.put("sub_type", null);
        returnDate.put("data", new ArrayList<CloudDevice>());

        // 使用Map分组
        Map<String, List<CloudDevice>> groupedDevices = new HashMap<>();
        for (CloudDevice cloudDevice : convert) {
            String subType = cloudDevice.getSubType();
            if (subType != null) {
                groupedDevices.computeIfAbsent(subType, k -> new ArrayList<>()).add(cloudDevice);
            }
        }

        //查询subType对应名称
        List<JSONObject> nameList = microsoftAzureMapper.getSubTypeName();
        Map<String, String> map = new HashMap<>();
        if (nameList != null && nameList.size() > 0) {
            //将nameList中的codeName和codeOtherName转换成map
            for (JSONObject jsonObject : nameList) {
                map.put(jsonObject.getString("code_other_name"), jsonObject.getString("code_name"));
            }
        }

        // 将分组结果放入返回的 JSON 对象和 List
        for (Map.Entry<String, List<CloudDevice>> entry : groupedDevices.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("subType", entry.getKey());
            jsonObject.put("subTypeName", map.get(entry.getKey()) != null ? map.get(entry.getKey()) : entry.getKey());
            jsonObject.put("data", entry.getValue());
            list.add(jsonObject);
        }
        return list;
    }

    //结果转Device集合
    private List<CloudDevice> convert(List<DeviceCloudTargetVO> vos) {
        List<CloudDevice> deviceList = new ArrayList<>();
        if (vos != null && vos.size() > 0) {
            for (DeviceCloudTargetVO target : vos) {
                CloudDevice device = new CloudDevice();
                device.setDeleted(false);
                device.setSuccess(false);
                device.setUuid(target.getId());
                device.setLogo(target.getLogo());
                device.setIsImport(false);
                device.setIsInternal(false);
                device.setIsInit(false);
                device.setIsBuildIn(false);
                device.setIsHide(false);

                device.setName(target.getName());
                device.setDefaultLaunchImageName(target.getName());
                device.setCreatedAt(String.valueOf(target.getCreateTime()));
                device.setCloudServiceType(target.getCloudServiceType());
                //云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储)
                switch (target.getCloudServiceType()) {
                    case "1":
                        device.setServiceType(target.getNetType());
                        device.setSubType(target.getSubType());
                        if (target.getNetType() != null && target.getNetType().equals("VNet")) {
                            cloudTargetConverter.convertExtNet(device, target);
                        } else if (target.getNetType() != null && target.getNetType().equals("fireWall")) {
                            device.setImageType("fireWall");
                        } else if (target.getNetType() != null && target.getNetType().equals("WAF")) {
                            device.setImageType("WAF");
                        }
                        break;
                    case "2":
                        device.setServiceType(target.getCalculateType());
                        device.setSubType(target.getSubType());
                        if (target.getSubType() != null && target.getSubType().equals("vm")) {
                            cloudTargetConverter.convertExtVm(device, target);
                        } else {
                        }
                        break;
                    case "3":
                        device.setServiceType(target.getContainerType());
                        device.setSubType(target.getSubType());
                        if (target.getSubType() != null && target.getSubType().equals("containerRegistry")) {
                            cloudTargetConverter.convertExtDocker(device, target);
                        } else {
                        }
                        break;
                    case "4":
                    case "5":
                    case "6":
                    case "7":
                    default:
                }
                deviceList.add(device);
            }
        }
        return deviceList;
    }

    @Async
    public void executeSequentially() {
        //1: 微软公有云 2:微软私有云 3:微软混合云
        JSONObject publicTargetResult = synchronousCloudData(1);
        JSONObject privateTargetResult = synchronousCloudData(2);
        JSONObject mixTargetResult = synchronousCloudData(3);
    }

    /**
     * 云镜像管理
     */
    @Transactional
    public JSONObject synchronousCloudData(Integer cloudType) {
        // 获取该订阅Id的资源管理器
        IAzureResourceManager instance = IAzureResourceManager.getInstance(cloudType);
        String cloudTypeStr;
        switch (cloudType) {
            case 1://公有云
                cloudTypeStr = "1004";
                break;
            case 2://私有云
                cloudTypeStr = "1003";
                break;
            case 3://混合云
                cloudTypeStr = "1002";
                break;
            default:
                cloudTypeStr = "1004";
        }

        //目前是根据配置的环境信息去获取微软云资源
        JSONObject result = new JSONObject();

        //获取微软云市场 网络服务(网络-网络服务)
        List getCloudInternetServices = getCloudInternetServices(instance);
        result.put("getCloudInternetServiceList", getCloudInternetServices);
        //获取用户上传 容器注册表(容器-容器基础结构-容器注册表)
        List dockerImages = getDockerImages(instance);
        result.put("dockerImageList", dockerImages);
        //获取用户上传 虚拟机镜像(计算-lass-映像)
        List localImages = getLocalImages(instance);
        result.put("localImageList", localImages);
        //获取 容器实例列表(容器-容器基础结构-容器实例)
        List dockerInstances = null;
//        List dockerInstances = getDockerInstances(instance);
//        result.put("dockerInstanceList", dockerInstances);
        //获取微软云市场 虚拟机镜像(计算-lass-映像)
        List images = null;
        images = getImages(instance);
        result.put("imageList", images);

        //云靶标主数据表
        List<DeviceCloudTargetPO> mainTargetList = new ArrayList<>();

        //网络表数据
        List<DeviceCloudTargetNet> netList = new ArrayList<>();
        cloudTargetConverter.cloudNetServiceConvert(cloudTypeStr, mainTargetList, netList, getCloudInternetServices);

        //容器表数据
        List<DeviceCloudTargetContainer> containerList = new ArrayList<>();
        cloudTargetConverter.cloudContainerRegistryConvert(cloudTypeStr, mainTargetList, containerList, dockerImages);//容器-容器基础结构-容器注册表
        cloudTargetConverter.cloudContainerInstanceConvert(cloudTypeStr, mainTargetList, containerList, dockerInstances);//容器-容器基础结构-容器实例

        //计算表数据
        List<DeviceCloudTargetCalculate> calculateList = new ArrayList<>();
        cloudTargetConverter.cloudCalculateSoftVMConvert(cloudTypeStr, mainTargetList, calculateList, images);//计算-laas-vm(微软云)
        cloudTargetConverter.cloudCalculateUploadVMConvert(cloudTypeStr, mainTargetList, calculateList, localImages);//计算-laas-vm(用户上传)

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", result);
        jsonObject.put("data", mainTargetList);
        jsonObject.put("code", "200");

        microsoftAzureMapper.deleteCloudTarget(null, cloudTypeStr);
        microsoftAzureMapper.deleteCloudTargetNet(null, cloudTypeStr);
        microsoftAzureMapper.deleteCloudTargetCalculate(null, cloudTypeStr);
        microsoftAzureMapper.deleteCloudTargetContainer(null, cloudTypeStr);

        Boolean batchInsertCloudTarget = batchInsert(mainTargetList, microsoftAzureMapper::batchInsertCloudTarget, "云靶标主数据");
        Boolean batchInsertCloudTargetNet = batchInsert(netList, microsoftAzureMapper::batchInsertCloudTargetNet, "网络表数据");
        Boolean batchInsertCloudTargetCalculate = batchInsert(calculateList, microsoftAzureMapper::batchInsertCloudTargetCalculate, "计算表数据");
        Boolean batchInsertCloudTargetContainer = batchInsert(containerList, microsoftAzureMapper::batchInsertCloudTargetContainer, "容器表数据");

        if (!batchInsertCloudTarget || !batchInsertCloudTargetNet || !batchInsertCloudTargetCalculate || !batchInsertCloudTargetContainer) {
            jsonObject.put("message", "同步数据批量保存有错误插入");
            jsonObject.put("code", "201");
        }
        return jsonObject;
    }

    private <T> boolean batchInsert(List<T> dataList, Function<List<T>, Boolean> insertFunction, String entityType) {
        if (CollectionUtils.isEmpty(dataList)) {
            return true;
        }
        AtomicInteger counter = new AtomicInteger(0);
        AtomicBoolean allSuccess = new AtomicBoolean(true);
        dataList.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / 1000))
                .values().forEach(subGroup -> {
                    List<T> subList = new ArrayList<>(subGroup);
                    boolean isInserted = insertFunction.apply(subList);
                    if (!isInserted) {
                        allSuccess.set(false);
                    }
                });
        return allSuccess.get();
    }

    /**
     * 获取网络服务
     */
    public List getCloudInternetServices(IAzureResourceManager instance) {
        //收集元素
        List<JSONObject> jsonList = new ArrayList<>();
        JSONObject VNet = new JSONObject();
        VNet.put("name", "VNet");
        VNet.put("netType", "basicNetwork");
        VNet.put("subType", "VNet");
        jsonList.add(VNet);
        JSONObject fireWall = new JSONObject();
        fireWall.put("name", "fireWall");
        fireWall.put("netType", "networkSecurity");
        fireWall.put("subType", "fireWall");
        jsonList.add(fireWall);
        JSONObject WAF = new JSONObject();
        WAF.put("name", "WAF");
        WAF.put("netType", "networkSecurity");
        WAF.put("subType", "WAF");
        jsonList.add(WAF);
        return jsonList;
    }

    /**
     * docker镜像--存放在容器注册表内
     */
    public List getDockerImages(IAzureResourceManager instance) {
        //收集元素
        List<JSONObject> jsonList = new ArrayList<>();
        // 获取该订阅下所有资源组中的容器注册表
        instance.getManager().containerRegistries().list().forEach(registry -> {
            if (registry != null) {
                // 获取并打印容器注册表的endpoint
                String endpoint = registry.loginServerUrl();
                System.out.println("Container Registry Endpoint: " + endpoint);
                endpoint = "https://" + endpoint;
                // 初始化ContainerRegistryClient
                ContainerRegistryClient containerRegistryClient = new ContainerRegistryClientBuilder()
                        .endpoint(endpoint)
                        .credential(instance.getTokenCredential())
                        .buildClient();

                //ContainerRegistryClient获取所有存储库
                PagedIterable<String> repositoryNames = containerRegistryClient.listRepositoryNames();
                for (String name : repositoryNames) {
                    // 获取名为该name的存储库
                    ContainerRepository helloWorldRepository = containerRegistryClient.getRepository(name);
                    // 获取存储库中的所有镜像（标签）
                    List<ArtifactManifestProperties> imageTags = helloWorldRepository.listManifestProperties().stream()
                            .collect(Collectors.toList());
                    // 打印镜像列表
                    for (ArtifactManifestProperties imageTag : imageTags) {
                        JSONObject jsonObject = new JSONObject();

                        System.out.println("repositoryName:" + name);
                        jsonObject.put("repositoryName", name);

                        System.out.println("Image Digest: " + imageTag.getDigest());
                        jsonObject.put("Image Digest", imageTag.getDigest());

                        System.out.println("Image Tags: " + imageTag.getTags());
                        jsonObject.put("Image Tags", imageTag.getTags());

                        System.out.println("--------------");
                        jsonList.add(jsonObject);
                    }
                }
            } else {
                System.out.println("Container registry not found.");
            }
        });
        return jsonList;
    }

    /**
     * docker镜像--容器实例
     */
    public List getDockerInstances(IAzureResourceManager instance) {
        //收集元素
        List<JSONObject> jsonList = new ArrayList<>();

        // 初始化容器实例管理器
        ContainerInstanceManager containerInstanceManager = ContainerInstanceManager
                .authenticate(instance.getTokenCredential(), new AzureProfile(AzureEnvironment.AZURE));

        // 获取所有容器组列表
        PagedIterable<ContainerGroup> containerGroups = containerInstanceManager.containerGroups().list();

        // 遍历容器组并打印容器实例信息
        for (ContainerGroup containerGroup : containerGroups) {
            System.out.println("Container Group Name: " + containerGroup.name());
            System.out.println("Region: " + containerGroup.regionName());
            System.out.println("Status: " + containerGroup.provisioningState());

            // 获取并打印该容器组中的所有容器实例
            Map<String, Container> containers = containerGroup.containers();
            for (Map.Entry<String, Container> entry : containers.entrySet()) {
                Container container = entry.getValue();
                //运行视图,包含容器的当前状态、重启次数、启动时间和结束时间
//                ContainerPropertiesInstanceView containerPropertiesInstanceView = container.instanceView();
                System.out.println("Container Name: " + container.name());
                System.out.println("Image: " + container.image());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("containerName", container.name());
                jsonObject.put("image", container.image());
                jsonList.add(jsonObject);
            }
        }
        return jsonList;
    }

    /**
     * 虚拟机市场镜像
     */
    public List getImages(IAzureResourceManager instance) {
        AzureResourceManager azureResourceManager = instance.getManager();
        // 指定要查询的区域
        Region region = Region.ASIA_EAST; // 替换为您的实际区域
        //只分地区的所有镜像
//        PagedIterable<VirtualMachineImage> virtualMachineImages = azureResourceManager.virtualMachineImages().listByRegion(region);

        List<JSONObject> result = new ArrayList<>();
//        try {
//            virtualMachineImages.forEach(image -> {
//                String publisherName = image.publisherName();
//                String offerName = image.offer();
//                String skuName = image.sku();
//                OperatingSystemTypes operatingSystemTypes = image.osDiskImage().operatingSystem();
//
//                JSONObject third = new JSONObject();
//                third.put("imageName", offerName);
//                third.put("imageVersion", image.version());
//                third.put("PublisherName", publisherName);
//                third.put("offerName", offerName);
//                third.put("skuName", skuName);
//                third.put("operatingSystem", operatingSystemTypes);
//
//                result.add(third);
//            });
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }


//        // 获取所有虚拟机发布者
        PagedIterable<VirtualMachinePublisher> virtualMachinePublishers = azureResourceManager.virtualMachineImages().publishers().listByRegion(region);
        for (VirtualMachinePublisher publisher : virtualMachinePublishers) {
            //todo 筛选出需要的VM镜像
            System.out.println("PublisherName: " + publisher.name());
            if (publisher.name() != null && (publisher.name().contains("Canonical") || publisher.name().contains("canonical"))) {
                // 获取发布者的所有产品(offer)
                VirtualMachineOffers offers = publisher.offers();
                PagedIterable<VirtualMachineOffer> list = offers.list();
                int firstTime = 0;
                for (VirtualMachineOffer offer : list) {
//                    if (firstTime < 10) {
//                        firstTime++;

                    //休眠0.5秒,请求速度过快会导致请求微软云的连接中断
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("offerName: " + offer.name());
                    // 获取该产品的所有SKU
                    VirtualMachineSkus skus = offer.skus();
                    PagedIterable<VirtualMachineSku> skuList = skus.list();
                    int secondTime = 0;
                    for (VirtualMachineSku sku : skuList) {
//                            if (secondTime < 10) {
//                                secondTime++;

                        //休眠0.5秒,请求速度过快会导致请求微软云的连接中断
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        System.out.println("skuName: " + sku.name());
                        // 获取每个SKU的所有版本（image versions）
                        PagedIterable<VirtualMachineImage> imageList = sku.images().list();
                        int thirdTime = 0;
                        for (VirtualMachineImage image : imageList) {
                            if (thirdTime < 1) {
                                thirdTime++;

                                //休眠0.5秒,请求速度过快会导致请求微软云的连接中断
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                JSONObject forth = new JSONObject();
                                forth.put("imageName", offer.name());
                                forth.put("imageVersion", image.version());
                                forth.put("PublisherName", publisher.name());
                                forth.put("offerName", offer.name());
                                forth.put("skuName", sku.name());
                                forth.put("operatingSystem", image.osDiskImage().operatingSystem());
                                result.add(forth);
                            }
                        }
//                            }
                    }
//                    }
                }
            }
        }
        return result;
    }

    /**
     * 虚拟机镜像--暂定存放在映像内
     */
    public List getLocalImages(IAzureResourceManager instance) {
        AzureResourceManager azureResourceManager = instance.getManager();
        // 获取所有自定义虚拟机映像
        VirtualMachineCustomImages virtualMachineCustomImages = azureResourceManager.virtualMachineCustomImages();
        PagedIterable<VirtualMachineCustomImage> list = virtualMachineCustomImages.list();

        List<JSONObject> imageList = new ArrayList<>();
        // 打印上传的映像列表
        for (VirtualMachineCustomImage image : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("imageName", image.name());
            jsonObject.put("uploadImageId", image.id());
            jsonObject.put("operatingSystem", image.osDiskImage().osType());
            jsonObject.put("imageVersion", "0.0.1");
            System.out.println("uploadImageName: " + image.name());
            System.out.println("-------------");
            imageList.add(jsonObject);
        }
        return imageList;
    }

    /**
     * 云镜像删除
     */
    @Transactional
    public Boolean deleteCloudTarget(JSONObject requestData) {
        if (requestData != null && (requestData.get("id") != null && !requestData.get("id").toString().equals("") ||
                requestData.get("ids") != null)) {
            String id = "";
            if (requestData.get("id") != null && !requestData.get("id").toString().equals("")) {
                id = requestData.get("id").toString();
            }
            List<String> ids = null;
            if (requestData.get("ids") != null && requestData.get("ids") instanceof List) {
                ids = (List<String>) requestData.get("ids");
            }
            Integer deleteByPk = microsoftAzureMapper.deleteByIds(id, ids);
            if (deleteByPk > 0) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public Boolean updateCloudTarget(JSONObject requestData) {
        if (requestData != null && requestData.get("id") != null && !requestData.get("id").toString().equals("")) {
            Boolean updateCloudTarget = microsoftAzureMapper.updateCloudTarget(requestData.get("id").toString(), requestData.getInteger("cloudServiceType"), requestData);
            return updateCloudTarget;
        }
        return false;
    }

    public DeviceCloudTargetVO getCloudTargetById(JSONObject requestData) {
        DeviceCloudTargetVO data = new DeviceCloudTargetVO();
        if (requestData.get("cloudServiceType").toString().equals("2")) {
            data = microsoftAzureMapper.getCloudTargetCalculateById(requestData.get("id").toString());
        } else if (requestData.get("cloudServiceType").toString().equals("3")) {
            data = microsoftAzureMapper.getCloudTargetContainerById(requestData.get("id").toString());
        } else if (requestData.get("cloudServiceType").toString().equals("1")) {
            data = microsoftAzureMapper.getCloudTargetNetById(requestData.get("id").toString());
        }
        return data;
    }

    /**
     * 查询所有镜像管理分类
     */
    public List getImageCategories(String cloudCode) {
        if (cloudCode == null || cloudCode.equals("")) {
            cloudCode = ParentCodeEnum.PARENT_MIXCLOUD_CATEGORY_TYPE.getCode();
//        PARENT_MIXCLOUD_CATEGORY_TYPE("1002", "混合云配置项菜单"),
//        PARENT_PRIVATECLOUD_CATEGORY_TYPE("1003", "私有云配置项菜单"),
//        PARENT_PUBLICCLOUD_CATEGORY_TYPE("1004", "公有云配置项菜单");
        }

        //查询所有数据
        List<MpSysCodeMenu> data = microsoftAzureMapper.getAlldata(cloudCode);
        //根据parentCode字段将data数据按照父子关系处理返回新的List<MpSysCodeMenu>数据
        List<MpSysCodeMenu> dtoTreeList = getDtoTreeList(data, cloudCode);
        return dtoTreeList;
    }

    /**
     * 将普通的entity的集合转成存在树状结构的集合
     *
     * @param list
     * @return
     */
    public static List<MpSysCodeMenu> getDtoTreeList(List<MpSysCodeMenu> list, String parentCode) {
        List<MpSysCodeMenu> dtos = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            return dtos;
        }
        for (MpSysCodeMenu menu : list) {
            if (parentCode.equals(menu.getParentCode())) {
                //找到顶层目录
                MpSysCodeMenu menuDto = new MpSysCodeMenu();
                //将实体类的数据转成dto
                BeanUtils.copyProperties(menu, menuDto);
                //获取该实体类下的所有子级
                dtos.add(findChildren(menuDto, list));
            }
        }
        return dtos;
    }

    /**
     * 获取dto类下面的所有子级,存到dto中的children中
     *
     * @param menuDto
     * @param list
     * @return
     */
    private static MpSysCodeMenu findChildren(MpSysCodeMenu menuDto, List<MpSysCodeMenu> list) {
        for (MpSysCodeMenu menu : list) {
            if (menuDto.getCode().equals(menu.getParentCode())) {
                //此时menu是menuDto下的子级
                if (menuDto.getChildren() == null) {
                    menuDto.setChildren(new ArrayList<MpSysCodeMenu>());
                }
                MpSysCodeMenu dto = new MpSysCodeMenu();
                BeanUtils.copyProperties(menu, dto);
                //递归
                menuDto.getChildren().add(findChildren(dto, list));
            }
        }
        return menuDto;
    }

    /**
     * 查询常用靶标信息
     */
    public ListObjServiceResult getCommonTargets(EquipmentRequestVO request) {
        Integer offset = 0;
        Integer pageSize = 10;
        if (request.getPage() != null && request.getPerPage() != null) {
            offset = (request.getPage() - 1) * request.getPerPage();
            pageSize = request.getPerPage();
        }
        //sql同时修改
        List<MpDevopsBoundaryNetworkEquipment> list = microsoftAzureMapper.getCommonTargets(request, offset, pageSize);
        list.forEach(item -> {
            item.setImageType("networkEquipment");
        });
        Integer num = microsoftAzureMapper.getCommonTargetNum(request, offset, pageSize);
        return new ListObjServiceResult(list, num).asResult();
    }
}
