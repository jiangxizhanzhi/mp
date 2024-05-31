package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.auth.util.ParentCodeEnum;
import cn.cyberpeace.mp.base.bo.MpSysCodeMenu;
import cn.cyberpeace.mp.cscene.service.service.LoadServiceImpl;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devopscluster.enums.CommonTargetNameEnum;
import cn.cyberpeace.mp.devopscluster.po.*;
import cn.cyberpeace.mp.devopscluster.repository.MicrosoftAzureMapper;
import cn.cyberpeace.mp.devopscluster.request.TargetSceneRequest;
import cn.cyberpeace.mp.devopscluster.vo.DeviceVO;
import cn.cyberpeace.mp.devopscluster.vo.MpSysCodeMenuVO;
import cn.cyberpeace.mp.devopscluster.vo.TargerRequestVO;
import cn.cyberpeace.mp.devopscluster.vo.convert.DeviceVoConverter;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import com.alibaba.fastjson.JSONObject;
import com.azure.containers.containerregistry.ContainerRegistryClient;
import com.azure.containers.containerregistry.ContainerRegistryClientBuilder;
import com.azure.containers.containerregistry.ContainerRepository;
import com.azure.containers.containerregistry.models.ArtifactManifestProperties;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.management.Region;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MicrosoftAzureConverter implements BaseConverter {
    @Autowired
    private MicrosoftAzureMapper microsoftAzureMapper;

    @Autowired
    private TargetConverter targetConverter;

    @Autowired
    private DeviceVoConverter deviceVoConverter;

    @Autowired
    private LoadServiceImpl loadService;

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

    private JSONObject getReturnData(String cloudServiceType, String tableName, TargerRequestVO request) {
        List<MpSysCodeMenuVO> codes = request.getCodes();
        List<MpSysCodeMenu> sqls = null;
        if (codes != null && codes.size() > 0) {
            sqls = microsoftAzureMapper.getSqlByCodes(codes);
            for (int i = 0; i < sqls.size(); i++) {
                if (sqls.get(i).getSupplement() != null && !sqls.get(i).getSupplement().equals("") && sqls.get(i).getSupplement().contains("**")) {
                    for (int j = 0; j < codes.size(); j++) {
                        if (codes.get(j).getCode() != null && sqls.get(i).getCode() != null && codes.get(j).getCode().equals(sqls.get(i).getCode())) {
                            if (codes.get(j).getValue() != null && !codes.get(j).getValue().equals("")) {
                                sqls.get(i).setSupplement("and " + sqls.get(i).getSupplement().replace("**", codes.get(j).getValue()));
                            } else {
                                sqls.get(i).setSupplement("");
                            }
                        }
                    }
                } else if (sqls.get(i).getSupplement() != null && !sqls.get(i).getSupplement().equals("")) {
                    if (sqls.get(i).getSupplement().contains(".")) {
                        sqls.get(i).setSupplement("and " + sqls.get(i).getSupplement());
                    } else {
                        sqls.get(i).setSupplement("and " + tableName + "." + sqls.get(i).getSupplement());
                    }
                }
            }
        }
        List<MpSysCodeMenu> filteredList = null;
        if (sqls != null && sqls.size() > 0) {
            filteredList = sqls.stream()
                    .filter(s -> s.getSupplement() != null && !s.getSupplement().isEmpty())
                    .collect(Collectors.toList());
        }
        Integer offset = 0;
        Integer pageSize = 10;
        if (request.getPage() != null && request.getPerPage() != null) {
            offset = (request.getPage() - 1) * request.getPerPage();
            pageSize = request.getPerPage();
        }
        //需要同时修改sql
        List<DeviceCloudTargetVO> list = microsoftAzureMapper.getCloudTargetListByCodes(Integer.parseInt(cloudServiceType), filteredList, offset, pageSize);
        Integer totalNum = microsoftAzureMapper.getCloudTargetListByCodesNum(Integer.parseInt(cloudServiceType), filteredList);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", list);
        jsonObject.put("total", totalNum);
        return jsonObject;
    }

    public JSONObject getCloudTargetListByCodes(TargerRequestVO request) {
        List<DeviceCloudTargetVO> vos = null;
        Integer total = 0;
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
        List<CloudDevice> convert = convert(vos, total);
        JSONObject result = new JSONObject();
        result.put("list", convert);
        result.put("total", total);
        return result;
    }

    //结果转Device集合
    private List<CloudDevice> convert(List<DeviceCloudTargetVO> vos, Integer total) {
        List<CloudDevice> deviceList = new ArrayList<>();
        if(vos != null && vos.size() > 0){
            for (int i = 0; i < vos.size(); i++) {
                CloudDevice device = new CloudDevice();
                device.setDeleted(false);
                device.setId(vos.get(i).getId());
                device.setCloudServiceType(vos.get(i).getCloudServiceType());
                device.setName(vos.get(i).getName());
                device.setOtherName(vos.get(i).getOtherName());
                device.setLogo(vos.get(i).getLogo());
                //云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储 )
                switch (vos.get(i).getCloudServiceType()) {
                    case "1":
                        device.setServiceType(vos.get(i).getNetType());
                        device.setSubType(vos.get(i).getSubType());
                        NetService netService = new NetService();
                        netService.setPk(vos.get(i).getPk());
                        netService.setId(vos.get(i).getId());
                        netService.setSourceType(vos.get(i).getSourceType());
                        device.setNetService(netService);
                        break;
                    case "2":
                        device.setServiceType(vos.get(i).getCalculateType());
                        device.setSubType(vos.get(i).getSubType());
                        CalculateLaasVM calculateLaasVM = new CalculateLaasVM();
                        calculateLaasVM.setCalculateType(vos.get(i).getCalculateType());
                        calculateLaasVM.setSubType(vos.get(i).getSubType());
                        calculateLaasVM.setPk(vos.get(i).getPk());
                        calculateLaasVM.setId(vos.get(i).getId());
                        calculateLaasVM.setPublisher(vos.get(i).getPublisher());
                        calculateLaasVM.setOffer(vos.get(i).getOffer());
                        calculateLaasVM.setSku(vos.get(i).getSku());
                        calculateLaasVM.setOperatingSystem(vos.get(i).getOperatingSystem());
                        calculateLaasVM.setSourceType(vos.get(i).getSourceType());
                        //todo 待获取属性
                        calculateLaasVM.setLoopholes(null);

                        device.setCalculateLaasVM(calculateLaasVM);
                        break;
                    case "3":
                        device.setServiceType(vos.get(i).getContainerType());
                        device.setSubType(vos.get(i).getSubType());
                        DockerInfrastructureContainerRegistries registries = new DockerInfrastructureContainerRegistries();
                        registries.setPk(vos.get(i).getPk());
                        registries.setId(vos.get(i).getId());
                        registries.setContainerType(vos.get(i).getContainerType());
                        registries.setSubType(vos.get(i).getSubType());
                        registries.setSourceType(vos.get(i).getSourceType());
                        registries.setUploadTargetName(vos.get(i).getUploadTargetName());
                        registries.setOperatingSystem(vos.get(i).getOperatingSystem());
                        device.setDockerInfrastructureContainerRegistries(registries);
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

    /**
     * 云镜像管理
     */
    @Transactional
    public JSONObject synchronousCloudData(Integer cloudType) {

        // 获取资源管理器
        IAzureResourceManager instance = IAzureResourceManager.getInstance(cloudType);

        //目前是根据配置的环境信息去获取微软云资源
        JSONObject result = new JSONObject();
        //todo 将数据同步至数据表中
        List<DeviceCloudTargetPO> list = new ArrayList<>();

        //获取微软云网络服务
        List getCloudInternetServices = getCloudInternetServices(instance);
        result.put("getCloudInternetServices", getCloudInternetServices);
        //获取用户 docker镜像
        List dockerImages = getDockerImages(instance);
        result.put("dockerImageList", dockerImages);
        //获取微软云 docker镜像
        List cloudDockerImages = getCloudDockerImages(instance);
        result.put("cloudDockerImageList", cloudDockerImages);
        //获取微软云 虚拟机镜像
        List images = getImages(instance);
        result.put("imagesList", images);
        //获取用户 虚拟机镜像
        List localImages = getLocalImages(instance);
        result.put("localImagesList", localImages);
        //获取微软云存储服务
        List microsoftCloudStorageServiceList = getMicrosoftCloudStorageServiceList();
        result.put("microsoftCloudStorageServiceList", microsoftCloudStorageServiceList);

        //网络表数据
        List<DeviceCloudTargetNet> netList = new ArrayList<>();

        //网络服务
        for(int i = 0; i < getCloudInternetServices.size(); i++){
            //主表元素
            DeviceCloudTargetPO deviceCloudTarget = new DeviceCloudTargetPO();
            String id = String.valueOf(UUID.randomUUID());
            deviceCloudTarget.setId(id);
            deviceCloudTarget.setName(((JSONObject) getCloudInternetServices.get(i)).get("netServiceName") != null ? ((JSONObject) getCloudInternetServices.get(i)).get("netServiceName").toString() : "");
            deviceCloudTarget.setOtherName(((JSONObject) getCloudInternetServices.get(i)).get("netServiceName") != null ? ((JSONObject) getCloudInternetServices.get(i)).get("netServiceName").toString() : "");
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

        //容器表数据
        List<DeviceCloudTargetContainer> containerList = new ArrayList<>();

        //docker镜像
        for (int i = 0; i < dockerImages.size(); i++) {
            //主表元素
            DeviceCloudTargetPO deviceCloudTarget = new DeviceCloudTargetPO();
            deviceCloudTarget.setName(((JSONObject) dockerImages.get(i)).get("repositoryName") != null ? ((JSONObject) dockerImages.get(i)).get("repositoryName").toString() : "");
            deviceCloudTarget.setOtherName(((JSONObject) dockerImages.get(i)).get("repositoryName") != null ? ((JSONObject) dockerImages.get(i)).get("repositoryName").toString() : "");
            String id = String.valueOf(UUID.randomUUID());
            deviceCloudTarget.setId(id);
            //云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储
            deviceCloudTarget.setCloudServiceType(3);
            deviceCloudTarget.setOperatingSystem("linux");
            deviceCloudTarget.setOperatingSystemStatus("专业化");
            deviceCloudTarget.setGeneration("第一代");
            deviceCloudTarget.setVersion("0.0.1");
            list.add(deviceCloudTarget);

            //容器副表元素
            DeviceCloudTargetContainer deviceCloudTargetContainer = new DeviceCloudTargetContainer();
            deviceCloudTargetContainer.setLinkId(id);
            deviceCloudTargetContainer.setName(((JSONObject) dockerImages.get(i)).get("repositoryName") != null ? ((JSONObject) dockerImages.get(i)).get("repositoryName").toString() : "");
            deviceCloudTargetContainer.setSourceType(2);
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

        //计算表数据
        List<DeviceCloudTargetCalculate> calculateList = new ArrayList<>();

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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", result);
        jsonObject.put("data", list);
        jsonObject.put("code", "200");
        microsoftAzureMapper.deleteCloudTarget(null);
        microsoftAzureMapper.deleteCloudTargetNet(null);
        microsoftAzureMapper.deleteCloudTargetCalculate(null);
        microsoftAzureMapper.deleteCloudTargetContainer(null);
        //保存同步数据
        Boolean batchInsertCloudTarget = true;
        if (list != null && list.size() > 0) {
            //list按照一千数量间隔批量插入数据
            for (int i = 0; i < list.size(); i += 1000) {
                List<DeviceCloudTargetPO> subList = list.subList(i, i + 1000 > list.size() ? list.size() : i + 1000);
                Boolean insertFlag = microsoftAzureMapper.batchInsertCloudTarget(subList);
                if (!insertFlag) {
                    batchInsertCloudTarget = false;
                }
            }
        }
        Boolean batchInsertCloudTargetNet = true;
        if (netList != null && netList.size() > 0) {
            //list按照一千数量间隔批量插入数据
            for (int i = 0; i < netList.size(); i += 1000) {
                List<DeviceCloudTargetNet> subList = netList.subList(i, i + 1000 > netList.size() ? netList.size() : i + 1000);
                Boolean insertFlag = microsoftAzureMapper.batchInsertCloudTargetNet(subList);
                if (!insertFlag) {
                    batchInsertCloudTargetNet = false;
                }
            }
        }
        Boolean batchInsertCloudTargetCalculate = true;
        if (calculateList != null && calculateList.size() > 0) {
            //list按照一千数量间隔批量插入数据
            for (int i = 0; i < calculateList.size(); i += 1000) {
                List<DeviceCloudTargetCalculate> subList = calculateList.subList(i, i + 1000 > calculateList.size() ? calculateList.size() : i + 1000);
                Boolean insertFlag = microsoftAzureMapper.batchInsertCloudTargetCalculate(subList);
                if (!insertFlag) {
                    batchInsertCloudTargetCalculate = false;
                }
            }
        }
        Boolean batchInsertCloudTargetContainer = true;
        if (containerList != null && containerList.size() > 0) {
            //list按照一千数量间隔批量插入数据
            for (int i = 0; i < containerList.size(); i += 1000) {
                List<DeviceCloudTargetContainer> subList = containerList.subList(i, i + 1000 > containerList.size() ? containerList.size() : i + 1000);
                Boolean insertFlag = microsoftAzureMapper.batchInsertCloudTargetContainer(subList);
                if (!insertFlag) {
                    batchInsertCloudTargetContainer = false;
                }
            }
        }
        if (!batchInsertCloudTarget || !batchInsertCloudTargetNet || !batchInsertCloudTargetCalculate || !batchInsertCloudTargetContainer) {
            jsonObject.put("message", "同步数据批量保存有错误插入");
            jsonObject.put("code", "201");
        }
        return jsonObject;
    }

    /**
     * 获取存储账号内所有内容
     */
    public List getCloudInternetServices(IAzureResourceManager instance) {
        //收集元素
        List<JSONObject> jsonList = new ArrayList<>();
        JSONObject VNet = new JSONObject();
        VNet.put("netServiceName","VNet");
        VNet.put("netType","VNet");
        jsonList.add(VNet);
        JSONObject fireWall = new JSONObject();
        fireWall.put("netServiceName","fireWall");
        fireWall.put("netType","fireWall");
        jsonList.add(fireWall);
        JSONObject WAF = new JSONObject();
        WAF.put("netServiceName","WAF");
        WAF.put("netType","WAF");
        jsonList.add(WAF);
        return jsonList;
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
    public List getDockerImages(IAzureResourceManager instance) {
        //收集元素
        List<JSONObject> jsonList = new ArrayList<>();
        // 获取特定的容器注册表
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
     * 获取微软云 docker镜像
     */
    public List getCloudDockerImages(IAzureResourceManager instance) {
        //todo 待定
        String endpoint = "https://registrytest0407.azurecr.io";
        // 初始化ContainerRegistryClient
        ContainerRegistryClient containerRegistryClient = new ContainerRegistryClientBuilder()
                .endpoint(endpoint)
                .credential(instance.getTokenCredential())
                .buildClient();

        //收集元素
        List<JSONObject> jsonList = new ArrayList<>();

        return jsonList;
    }

    /**
     * 虚拟机市场镜像
     */
    public List getImages(IAzureResourceManager instance) {
        AzureResourceManager azureResourceManager = instance.getManager();
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
//            if (publisher.name() != null && publisher.name().equals("Canonical")) {
            if (firstTime < 10) {
                firstTime++;
                System.out.println("PublisherName: " + publisher.name());
                // 获取发布者的所有产品(offer)
                VirtualMachineOffers offers = publisher.offers();
                PagedIterable<VirtualMachineOffer> list = offers.list();
                int secondTime = 0;
                for (VirtualMachineOffer offer : list) {
                    if (secondTime < 1) {
                        secondTime++;
                        System.out.println("offerName: " + offer.name());
                        // 获取该产品的所有SKU
                        VirtualMachineSkus skus = offer.skus();
                        PagedIterable<VirtualMachineSku> skuList = skus.list();
                        int thirdTime = 0;
                        for (VirtualMachineSku sku : skuList) {
                            if (thirdTime < 1) {
                                thirdTime++;
                                System.out.println("skuName: " + sku.name());
                                // 获取每个SKU的所有版本（image versions）
                                PagedIterable<VirtualMachineImage> imageList = sku.images().list();
                                int time = 0;
                                for (VirtualMachineImage image : imageList) {
                                    if (time < 1) {
                                        time++;
                                        OperatingSystemTypes operatingSystemTypes = image.osDiskImage().operatingSystem();
                                        JSONObject third = new JSONObject();
                                        third.put("imageName", publisher.name());
                                        third.put("imageVersion", image.version());
                                        third.put("PublisherName", publisher.name());
                                        third.put("offerName", offer.name());
                                        third.put("skuName", sku.name());
                                        third.put("operatingSystem", operatingSystemTypes);
                                        result.add(third);
                                    }
                                }
                            }
                        }
                    }
                }
            }
//            }
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
        // 打印我上传的映像列表
        for (VirtualMachineCustomImage image : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("imageName", image.name());
            jsonObject.put("operatingSystem", image.osDiskImage().osType());
            jsonObject.put("imageVersion", "0.0.1");
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
            Integer deleteCalculatesByIds = microsoftAzureMapper.deleteCalculatesByIds(id, ids);
            Integer deleteContainersByIds = microsoftAzureMapper.deleteContainersByIds(id, ids);
            if (deleteByPk > 0) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public Boolean updateCloudTarget(JSONObject requestData) {
        if (requestData != null && requestData.get("id") != null && !requestData.get("id").toString().equals("")) {
            Integer id = 0;
            if (requestData.get("cloudServiceType").toString().equals("2")) {
                id = microsoftAzureMapper.updateCloudCalculateTarget(requestData.get("id").toString(), requestData);
            } else if (requestData.get("cloudServiceType").toString().equals("3")) {
                id = microsoftAzureMapper.updateCloudContainerTarget(requestData.get("id").toString(), requestData);
            }
            if (id > 0) {
                return true;
            }
        }
        return false;
    }

    public DeviceCloudTargetVO getCloudTargetById(JSONObject requestData) {
        DeviceCloudTargetVO data = new DeviceCloudTargetVO();
        if (requestData.get("cloudServiceType").toString().equals("2")) {
            data = microsoftAzureMapper.getCloudTargetCalculateById(requestData.get("id").toString());
        } else if (requestData.get("cloudServiceType").toString().equals("3")) {
            data = microsoftAzureMapper.getCloudTargetContainerById(requestData.get("id").toString());
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
