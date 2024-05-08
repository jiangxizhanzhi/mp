package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.auth.service.MicrosoftAzureLoginService;
import cn.cyberpeace.mp.auth.util.ParentCodeEnum;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.CloudDevice;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.enums.CommonTargetNameEnum;
import cn.cyberpeace.mp.devopscluster.model.MpSysCodeEnum;
import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.MicrosoftAzureMapper;
import cn.cyberpeace.mp.devopscluster.request.TargetSceneRequest;
import cn.cyberpeace.mp.devopscluster.vo.DeviceVO;
import cn.cyberpeace.mp.devopscluster.vo.convert.DeviceVoConverter;
import com.alibaba.fastjson.JSONObject;
import com.azure.containers.containerregistry.ContainerRegistryClient;
import com.azure.containers.containerregistry.ContainerRegistryClientBuilder;
import com.azure.containers.containerregistry.ContainerRepository;
import com.azure.containers.containerregistry.models.ArtifactManifestProperties;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.Region;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MicrosoftAzureConverter implements BaseConverter {
    @Autowired
    private MicrosoftAzureMapper microsoftAzureMapper;

    @Autowired
    private MicrosoftAzureLoginService microsoftAzureLoginService;

    @Autowired
    private TargetConverter targetConverter;

    @Autowired
    private TypeConverter typeConverter;

    @Autowired
    private DeviceVoConverter deviceVoConverter;

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

    public JSONObject getCloudTargetListByCodes(List<JSONObject> codes) {
        List<MpSysCodeEnum> sqls = microsoftAzureMapper.getSqlByCodes(codes);
        for (int i = 0; i < sqls.size(); i++) {
            if (sqls.get(i).getSupplement() != null && sqls.get(i).getSupplement().contains("**")) {
                for (int j = 0; j < codes.size(); j++) {
                    if (codes.get(j).get("code") != null && sqls.get(i).getCode() != null &&
                            codes.get(j).get("code").toString().equals(sqls.get(i).getCode())) {
                        sqls.get(i).setSupplement(sqls.get(i).getSupplement().replace("**", codes.get(j).get("value").toString()));
                    }
                }
            }
        }
        List<MpSysCodeEnum> filteredList = sqls.stream()
                .filter(s -> s.getSupplement() != null && !s.getSupplement().isEmpty())
                .collect(Collectors.toList());
        List<DeviceCloudTargetPO> list = microsoftAzureMapper.getCloudTargetListByCodes(filteredList);
        //结果转Device集合
        List<CloudDevice> deviceList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CloudDevice device = new CloudDevice();
            device.setDeleted(false);
            device.setName(list.get(i).getName());
            device.setLogo(list.get(i).getLogo());
            //云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储 )
            switch (list.get(i).getCloudServiceType()) {
                case 1:
                case 2:
                    device.getExtCloudVm().setTargetPk(list.get(i).getPk());
                    device.getExtCloudVm().setPublisher(list.get(i).getPublisher());
                    device.getExtCloudVm().setOffer(list.get(i).getOffer());
                    device.getExtCloudVm().setSku(list.get(i).getSku());
                    device.getExtCloudVm().setImageOSType(list.get(i).getImageType());
                    device.getExtCloudVm().setSourceType(list.get(i).getSourceType());
                    break;
                case 3:
                    device.getExtCloudDocker().setSourceType(list.get(i).getSourceType());
                    device.getExtCloudDocker().setUploadImageName(list.get(i).getUploadTargetName());
                    break;
                case 4:
                case 5:
                case 6:
                case 7:
                default:
            }
            deviceList.add(device);
        }
        JSONObject result = new JSONObject();
        result.put("list", deviceList);
        result.put("total", deviceList.size());
        return result;
    }

    /**
     * 云镜像管理同步
     */
    @Transactional
    public JSONObject synchronousCloudData(String requestData) throws Exception {

//        // 获取资源管理器
//        var azureResourceManager = IAzureResourceManager.getInstance(1).getManager();

//        //获取当前微软云使用账号
//        JSONObject request = new JSONObject();
//        request.put("cloudType",1);
//        MicrosoftAzureUserModel localAzurer = microsoftAzureLoginService.getLocalAzurer(request);

        //目前是根据配置的环境信息去获取微软云资源
        JSONObject result = new JSONObject();
        //todo 将数据同步至数据表中
        List<DeviceCloudTargetPO> list = new ArrayList<>();

        //获取用户 docker镜像
        List dockerImages = getDockerImages();
        result.put("dockerImageList", dockerImages);
        //获取微软云 docker镜像
        List cloudDockerImages = getCloudDockerImages();
        result.put("cloudDockerImageList", cloudDockerImages);
        //获取微软云 虚拟机镜像
        List images = getImages();
        result.put("imagesList", images);
        //获取用户 虚拟机镜像
        List localImages = getLocalImages();
        result.put("localImagesList", localImages);
        //获取微软云存储服务
        List microsoftCloudStorageServiceList = getMicrosoftCloudStorageServiceList();
        result.put("microsoftCloudStorageServiceList", microsoftCloudStorageServiceList);

        //docker镜像
        for (int i = 0; i < dockerImages.size(); i++) {
            DeviceCloudTargetPO deviceCloudTarget = new DeviceCloudTargetPO();
            deviceCloudTarget.setName(((JSONObject) dockerImages.get(i)).get("repositoryName").toString());
            deviceCloudTarget.setUploadTargetName(((JSONObject) dockerImages.get(i)).get("repositoryName").toString());
            deviceCloudTarget.setImageType("container");
            //云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储
            deviceCloudTarget.setCloudServiceType(2);
            //源类型(1:云平台 2:用户上传)
            deviceCloudTarget.setSourceType(2);
            deviceCloudTarget.setOperatingSystemStatus("专业化");
            deviceCloudTarget.setGeneration("第一代");
            deviceCloudTarget.setVersion("0.0.1");
            list.add(deviceCloudTarget);
        }

        //微软云镜像
        for (int i = 0; i < images.size(); i++) {
            DeviceCloudTargetPO deviceCloudTarget = new DeviceCloudTargetPO();
            deviceCloudTarget.setName(((JSONObject)images.get(i)).get("imageName") != null ? ((JSONObject) images.get(i)).get("imageName").toString() : "");
            deviceCloudTarget.setPublisher(((JSONObject)images.get(i)).get("PublisherName") != null ? ((JSONObject) images.get(i)).get("PublisherName").toString() : "");
            deviceCloudTarget.setOffer(((JSONObject)images.get(i)).get("offerName") != null ? ((JSONObject) images.get(i)).get("offerName").toString() : "");
            deviceCloudTarget.setSku(((JSONObject)images.get(i)).get("skuName") != null ? ((JSONObject) images.get(i)).get("skuName").toString() : "");
            //镜像类型
            deviceCloudTarget.setImageType("vm");
            //操作系统
            deviceCloudTarget.setOperatingSystem(((JSONObject)images.get(i)).get("operatingSystem") != null ? ((JSONObject) images.get(i)).get("operatingSystem").toString() : "");
            //云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储
            deviceCloudTarget.setCloudServiceType(2);
            //源类型(1:云平台 2:用户上传)
            deviceCloudTarget.setSourceType(1);
            deviceCloudTarget.setOperatingSystemStatus("专业化");
            deviceCloudTarget.setGeneration("第一代");
            deviceCloudTarget.setVersion("0.0.1");
            list.add(deviceCloudTarget);
        }

        //微软云本地镜像
        for (int i = 0; i < localImages.size(); i++) {
            DeviceCloudTargetPO deviceCloudTarget = new DeviceCloudTargetPO();
            deviceCloudTarget.setName(((JSONObject) localImages.get(i)).get("imageName").toString());
            deviceCloudTarget.setImageType("vm");
            //云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储
            deviceCloudTarget.setCloudServiceType(2);
            //源类型(1:云平台 2:用户上传)
            deviceCloudTarget.setSourceType(2);
            deviceCloudTarget.setOperatingSystemStatus("专业化");
            deviceCloudTarget.setGeneration("第一代");
            deviceCloudTarget.setVersion("0.0.1");
            list.add(deviceCloudTarget);
        }
        //拿到所有数据库数据
        List<DeviceCloudTargetPO> cloudTargetList = microsoftAzureMapper.getCloudTargetList(null);
        //保存同步数据
        Boolean batchInsertCloudTarget = microsoftAzureMapper.batchInsertCloudTarget(list);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", result);
        jsonObject.put("data", list);
        jsonObject.put("code", "200");
        if(batchInsertCloudTarget){
            //删除已经存在的数据
            Boolean deleteCloudTarget = microsoftAzureMapper.deleteCloudTarget(cloudTargetList);
            if(!deleteCloudTarget){
                jsonObject.put("message", "原数据未删除,请重新同步");
                jsonObject.put("code", "201");
            }
        }else{
            jsonObject.put("message", "同步数据保存失败");
            jsonObject.put("code", "201");
        }
        return jsonObject;
    }

    /**
     * 获取存储账号内所有内容
     */
    public void getBlobContainer() {
//        /**
//         * 登录Azure门户
//         * 导航至你的存储账户
//         * 左侧导航栏找到并点击“访问密钥”
//         * 在那里，你会看到两个密钥集，任选其中一个，复制其“连接字符串”
//         * 构建连接字符串，格式形如 "DefaultEndpointsProtocol=https;AccountName=test0327container1;AccountKey=<account-key>;EndpointSuffix=<endpoint-suffix>"
//         */
//        String connectionString = "DefaultEndpointsProtocol=https;AccountName=test0327container1;AccountKey=FrZnVLup7lS/UXdvRiArDZe4V1yD9fnqkPqcHvVJZckIKDqaQWoGtBP46WqsuDemNQFxbEjaE0Yd+AStMsys2A==;EndpointSuffix=core.windows.net";
//
//        // 创建BlobServiceClient实例
//        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
//                .connectionString(connectionString)
//                .buildClient();
//
//        List<JSONObject> list = new ArrayList<>();
//        // 遍历所有容器
//        for (BlobContainerItem containerItem : blobServiceClient.listBlobContainers()) {
//            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerItem.getName());
//            // 列出容器内的所有Blob,默认情况下，listBlobs() 方法可能仅返回一部分结果，因为它支持分页
//            containerClient.listBlobs().forEach(blobItem -> {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("name",blobItem.getName());
//                jsonObject.put("mateData",blobItem.getMetadata());
//                jsonObject.put("tags",blobItem.getTags());
//                jsonObject.put("properties",blobItem.getProperties());
//                list.add(jsonObject);
//            });
//        }
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("当前存储账户内镜像列表",list);
//        jsonObject.put("code","200");
//        jsonObject.put("message","SUCCESS");
    }

    /**
     * docker镜像--暂定存放在容器注册表内
     */
    public List getDockerImages() {
        String endpoint = "https://registrytest0407.azurecr.io";
        // 使用DefaultAzureCredential进行身份验证，适用于多种环境，如本地开发、Azure Functions、App Service等
        // 如果需要特定的身份验证方式，可以使用对应的AzureIdentity类，如ClientSecretCredential等
        ContainerRegistryClient containerRegistryClient = new ContainerRegistryClientBuilder()
                .endpoint(endpoint)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();

        //收集元素
        List<JSONObject> jsonList = new ArrayList<>();

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
        return jsonList;
    }

    /**
     * 获取微软云 docker镜像
     */
    public List getCloudDockerImages() {
        String endpoint = "https://registrytest0407.azurecr.io";
        // 使用DefaultAzureCredential进行身份验证，适用于多种环境，如本地开发、Azure Functions、App Service等
        // 如果需要特定的身份验证方式，可以使用对应的AzureIdentity类，如ClientSecretCredential等
        ContainerRegistryClient containerRegistryClient = new ContainerRegistryClientBuilder()
                .endpoint(endpoint)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();

        //收集元素
        List<JSONObject> jsonList = new ArrayList<>();

        return jsonList;
    }

    /**
     * 虚拟机市场镜像
     */
    public List getImages() {
        String subscriptionId = "c87ba926-f7c2-4d53-8b8e-89245dc92e26"; // 替换为您的实际订阅ID

        // 使用DefaultAzureCredential进行身份验证，适用于多种环境，如本地开发、Azure Functions、App Service等
        // 如果需要特定的身份验证方式，可以使用对应的TokenCredential类，如ClientSecretCredential等
        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
        AzureResourceManager azureResourceManager = AzureResourceManager
                .authenticate(new DefaultAzureCredentialBuilder().build(), profile)
                .withSubscription(subscriptionId);

        // 指定要查询的区域
        Region region = Region.ASIA_EAST; // 替换为您的实际区域

        List<JSONObject> result = new ArrayList<>();

//        //只分地区的所有镜像
//        PagedIterable<VirtualMachineImage> virtualMachineImages = azureResourceManager.virtualMachineImages().listByRegion(region);
//        for(VirtualMachineImage virtualMachineImage : virtualMachineImages){
//            String publisherName = virtualMachineImage.publisherName();
//            String offerName = virtualMachineImage.offer();
//            String skuName = virtualMachineImage.sku();
//        }

        // 获取所有虚拟机发布者
        PagedIterable<VirtualMachinePublisher> virtualMachinePublishers = azureResourceManager.virtualMachineImages().publishers().listByRegion(region);
        int firstTime = 0;
        for (VirtualMachinePublisher publisher : virtualMachinePublishers) {
            //todo 筛选出需要的VM镜像
            if (publisher.name() != null && publisher.name().equals("Canonical")) {
                if (firstTime < 1) {
                    System.out.println("PublisherName: " + publisher.name());
                    // 获取发布者的所有产品(offer)
                    VirtualMachineOffers offers = publisher.offers();
                    PagedIterable<VirtualMachineOffer> list = offers.list();
                    firstTime++;
                    int secondTime = 0;
                    for (VirtualMachineOffer offer : list) {
                        if (secondTime < 5) {
                            secondTime++;
                            System.out.println("offerName: " + offer.name());
                            // 获取该产品的所有SKU
                            VirtualMachineSkus skus = offer.skus();
                            PagedIterable<VirtualMachineSku> skuList = skus.list();
                            for (VirtualMachineSku sku : skuList) {
                                System.out.println("skuName: " + sku.name());
                                // 获取每个SKU的所有版本（image versions）
                                PagedIterable<VirtualMachineImage> imageList = sku.images().list();
                                int time = 0;
                                for (VirtualMachineImage image : imageList) {
                                    if (time < 1) {
                                        OperatingSystemTypes operatingSystemTypes = image.osDiskImage().operatingSystem();
                                        JSONObject third = new JSONObject();
                                        third.put("imageName", publisher.name());
                                        third.put("imageVersion", image.version());
                                        third.put("PublisherName", publisher.name());
                                        third.put("offerName", offer.name());
                                        third.put("skuName", sku.name());
                                        third.put("operatingSystem", operatingSystemTypes);
                                        result.add(third);
                                        time++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 虚拟机镜像--暂定存放在映像内
     */
    public List getLocalImages() {

        // 替换为您的实际订阅ID
        String subscriptionId = "c87ba926-f7c2-4d53-8b8e-89245dc92e26";

        // 指定要查询的区域
        Region region = Region.US_EAST;

        // 使用DefaultAzureCredential进行身份验证，适用于多种环境，如本地开发、Azure Functions、App Service等
        // 如果需要特定的身份验证方式，可以使用对应的TokenCredential类，如ClientSecretCredential等
        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
        AzureResourceManager azureResourceManager = AzureResourceManager
                .authenticate(new DefaultAzureCredentialBuilder().build(), profile)
                .withSubscription(subscriptionId);

        // 获取所有自定义虚拟机映像
        VirtualMachineCustomImages virtualMachineCustomImages = azureResourceManager.virtualMachineCustomImages();
        PagedIterable<VirtualMachineCustomImage> list = virtualMachineCustomImages.list();

        List<JSONObject> imageList = new ArrayList<>();
        // 打印我上传的映像列表
        for (VirtualMachineCustomImage image : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("imageName", image.name());
            System.out.println("imageName: " + image.name());
            System.out.println("-------------");
            imageList.add(jsonObject);
        }
        return imageList;
    }

    /**
     * 微软云存储服务列表
     */
    public List getMicrosoftCloudStorageServiceList() {

        return null;
    }

    /**
     * 云镜像删除
     */
    @Transactional
    public Boolean deleteCloudTarget(JSONObject requestData) {
        if (requestData != null && (requestData.get("pk") != null && !requestData.get("pk").toString().equals("") ||
                                    requestData.get("pks") != null)) {
            List<String> pks = null;
            if(requestData.get("pks") != null && requestData.get("pks") instanceof List){
                pks = (List<String>) requestData.get("pks");
            }
            Integer pk = microsoftAzureMapper.deleteByPk(requestData.get("pk").toString(),pks);
            if (pk > 0) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public Boolean updateCloudTarget(JSONObject requestData) {
        if (requestData != null && requestData.get("pk") != null && !requestData.get("pk").toString().equals("")) {
            Integer pk = microsoftAzureMapper.updateCloudTarget(requestData.get("pk").toString(), requestData);
            if (pk > 0) {
                return true;
            }
        }
        return false;
    }

    public DeviceCloudTargetPO getCloudTargetByPk(JSONObject requestData) {
        DeviceCloudTargetPO data = microsoftAzureMapper.getCloudTargetByPk(requestData.get("pk").toString());
        return data;
    }

    /**
     * 查询所有镜像管理分类
     */
    public List getImageCategories(String cloudCode) {
        if(cloudCode == null || cloudCode.equals("")){
            cloudCode = ParentCodeEnum.PARENT_MIXCLOUD_CATEGORY_TYPE.getCode();
//        PARENT_MIXCLOUD_CATEGORY_TYPE("1002", "混合云配置项菜单"),
//        PARENT_PRIVATECLOUD_CATEGORY_TYPE("1003", "私有云配置项菜单"),
//        PARENT_PUBLICCLOUD_CATEGORY_TYPE("1004", "公有云配置项菜单");
        }

        //查询所有数据
        List<MpSysCodeEnum> data = microsoftAzureMapper.getAlldata(cloudCode);
        //根据parentCode字段将data数据按照父子关系处理返回新的List<MpSysCodeEnum>数据
        List<MpSysCodeEnum> dtoTreeList = getDtoTreeList(data, cloudCode);
        return dtoTreeList;
    }

    /**
     * 将普通的entity的集合转成存在树状结构的集合
     *
     * @param list
     * @return
     */
    public static List<MpSysCodeEnum> getDtoTreeList(List<MpSysCodeEnum> list, String parentCode) {
        List<MpSysCodeEnum> dtos = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            return dtos;
        }
        for (MpSysCodeEnum menu : list) {
            if (parentCode.equals(menu.getParentCode())) {
                //找到顶层目录
                MpSysCodeEnum menuDto = new MpSysCodeEnum();
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
    private static MpSysCodeEnum findChildren(MpSysCodeEnum menuDto, List<MpSysCodeEnum> list) {
        for (MpSysCodeEnum menu : list) {
            if (menuDto.getCode().equals(menu.getParentCode())) {
                //此时menu是menuDto下的子级
                if (menuDto.getChildren() == null) {
                    menuDto.setChildren(new ArrayList<MpSysCodeEnum>());
                }
                MpSysCodeEnum dto = new MpSysCodeEnum();
                BeanUtils.copyProperties(menu, dto);
                //递归
                menuDto.getChildren().add(findChildren(dto, list));
            }
        }
        return menuDto;
    }

//    /**
//     * 查询所有信息(带过滤条件)--操作系统、应用系统、网络设备、安全设备内靶标
//     */
//    public List getTarget(TargetSceneRequest request) {
//        BasePageBO<Device> targetSceneConvertModel = allTarget(new ImagePageDTO(request));
//        List<Device> deviceList = targetSceneConvertModel.getList();
//        return deviceList;
//    }

//    /**
//     * 查询所有靶标数据
//     * @param dto
//     * @return
//     */
//    public BasePageBO<Device> allTarget(ImagePageDTO dto) {
//        BasePageBO<DeviceTargetPO> targetPageModel = this.page(dto);
//
//        List<DeviceTargetPO> deviceTargetPOS = targetPageModel.getList();
//        Map<String,DeviceTargetPO> supportExports = new HashMap<>();
//        deviceTargetPOS.stream().forEach(d -> {
//            supportExports.put(d.getUuid(), d);
//        });
//
//        List<Integer> ids = deviceTargetPOS.stream().map(DeviceTargetPO::getId).map(Integer::parseInt).collect(Collectors.toList());
//
//        BasePageBO<Device> sceneConvertModel = new BasePageBO<>();
//        sceneConvertModel.setTotal(targetPageModel.getTotal());
//        if (CollectionUtils.isEmpty(ids)){
//            sceneConvertModel.setList(Lists.newArrayList());
//            return sceneConvertModel;
//        }
//        CloudPlatformPageResult<Device> model = deviceService.findByIds(ids);
//
//        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getList())){
//            sceneConvertModel.setList(Lists.newArrayList());
//            return sceneConvertModel;
//        }
//        for (var d : model.getList()) {
//            if (ImageTypeEnum.CONTAINER.toValue().equals(d.getImageType()) && supportExports.containsKey(d.getUuid())) {
//                if (d.getExtDocker() != null) {
//                    d.getExtDocker().setInitSupport(supportExports.get(d.getUuid()).getInitSupport());
//                }
//            }
//        }
//        POLogoUtils.changeLogoPathToUrl(model.getList());
//        sceneConvertModel.setList(model.getList());
//        return sceneConvertModel;
//    }

//    /**
//     * 分页查询
//     * @param dto
//     * @return
//     */
//    public BasePageBO<DeviceTargetPO> page(ImagePageDTO dto) {
//        Integer total = devopsTargetService.totalCount(dto);
//        BasePageBO<DeviceTargetPO> targetPageModel = new BasePageBO<>();
//        targetPageModel.setTotal(total);
//        if (total == 0){
//            targetPageModel.setList(Lists.newArrayList());
//            return targetPageModel;
//        }
//        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.page(dto);
//        POLogoUtils.changeLogoPathToUrl(deviceTargetPOS);
//        targetPageModel.setList(deviceTargetPOS);
//        return targetPageModel;
//    }

    /**
     * 查询常用靶标信息
     */
    public List<DeviceVO> getCommonTargets(TargetSceneRequest request) {
        List<Device> deviceList = targetConverter.getCommonTargets("");
        List<DeviceVO> deviceVOList = deviceVoConverter.sourceToTarget(deviceList);

        Locale locale = LocaleContextHolder.getLocale();
        if (Objects.equals(locale, Locale.CHINA)) {
            // 常用靶标名称国际化处理
            deviceVOList.forEach(device -> {
                device.setImage(device.getName());
                device.setName(
                        Objects.nonNull(CommonTargetNameEnum.map.get(device.getName())) ?
                                CommonTargetNameEnum.map.get(device.getName()).toValue() : device.getName());
            });
        }

        if (Objects.nonNull(request.getImageType())) {
            deviceVOList = deviceVOList.stream().filter(v -> v.getImageType().equals(request.getImageType().toValue())).collect(Collectors.toList());
        }
        if (Objects.nonNull(request.getRole())) {
            deviceVOList = deviceVOList.stream().filter(deviceVO -> deviceVO.getRole().equals(request.getRole())).collect(Collectors.toList());
        }
        return deviceVOList;
    }
}
