package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.auth.model.MicrosoftAzureUserModel;
import cn.cyberpeace.mp.auth.util.ParentCodeEnum;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeAllConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeConvertModel;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.enums.CommonTargetNameEnum;
import cn.cyberpeace.mp.devopscluster.model.MpSysCodeEnum;
import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.MicrosoftAzureMapper;
import cn.cyberpeace.mp.devopscluster.request.TargetSceneRequest;
import cn.cyberpeace.mp.devopscluster.vo.DeviceVO;
import cn.cyberpeace.mp.devopscluster.vo.TypeAllVO;
import cn.cyberpeace.mp.devopscluster.vo.convert.DeviceVoConverter;
import cn.cyberpeace.mp.sceneplugincloud.plugin.azure.manager.IAzureResourceManager;
import com.alibaba.fastjson.JSONObject;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobContainerItem;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
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
public class MicrosoftAzureConverter implements BaseConverter{
    @Autowired
    private MicrosoftAzureMapper microsoftAzureMapper;

    @Autowired
    private TargetConverter targetConverter;

    @Autowired
    private TypeConverter typeConverter;

    @Autowired
    private DeviceVoConverter deviceVoConverter;

    /**
     * 云镜像管理页面分页查询
     */
    public List getCloudTargetList(JSONObject requestData) {
        Integer pageNum = requestData.get("pageNum") != null ? requestData.getInteger("pageNum") : 1;
        Integer pageSize = requestData.get("pageSize") != null ? requestData.getInteger("pageSize") : 10;
        requestData.put("offset", (pageNum - 1) * pageSize);
        requestData.put("pageSize",pageSize);
        List<DeviceCloudTargetPO> list = microsoftAzureMapper.getCloudTargetList(requestData);
        return list;
    }

    /**
     * 操作系统下拉框
     */
    public List<JSONObject> getOS() {
        List<JSONObject> oslist = microsoftAzureMapper.getSingleLayerList(ParentCodeEnum.PARENT_SYSTEM_TYPE.getCode());
        return oslist;
    }

    /**
     * 云镜像管理同步
     */
    @Transactional
    public JSONObject synchronousCloudData(JSONObject requestData) throws Exception {
//        // 获取资源管理器
//        var azureResourceManager = IAzureResourceManager.getInstance(1).getManager();

        //todo 暂不清楚镜像是否存放在存储账户内

        //获取当前微软云使用账号
        MicrosoftAzureUserModel azureUser = IAzureResourceManager.getAzureUser(1);
        /**
         * 登录Azure门户
         * 导航至你的存储账户
         * 左侧导航栏找到并点击“访问密钥”
         * 在那里，你会看到两个密钥集，任选其中一个，复制其“连接字符串”
         * 构建连接字符串，格式形如 "DefaultEndpointsProtocol=https;AccountName=test0327container1;AccountKey=<account-key>;EndpointSuffix=<endpoint-suffix>"
         */
        String connectionString = "DefaultEndpointsProtocol=https;AccountName=test0327container1;AccountKey=FrZnVLup7lS/UXdvRiArDZe4V1yD9fnqkPqcHvVJZckIKDqaQWoGtBP46WqsuDemNQFxbEjaE0Yd+AStMsys2A==;EndpointSuffix=core.windows.net";

        // 创建BlobServiceClient实例
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        List<JSONObject> list = new ArrayList<>();
        // 遍历所有容器
        for (BlobContainerItem containerItem : blobServiceClient.listBlobContainers()) {
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerItem.getName());
            // 列出容器内的所有Blob,默认情况下，listBlobs() 方法可能仅返回一部分结果，因为它支持分页
            containerClient.listBlobs().forEach(blobItem -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name",blobItem.getName());
                jsonObject.put("mateData",blobItem.getMetadata());
                jsonObject.put("tags",blobItem.getTags());
                jsonObject.put("properties",blobItem.getProperties());
                list.add(jsonObject);
            });
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("当前存储账户内镜像列表",list);
        jsonObject.put("code","200");
        jsonObject.put("message","SUCCESS");
        //处理返回结果
        return jsonObject;
    }

    /**
     * 云镜像删除
     */
    @Transactional
    public Boolean deleteCloudTarget(JSONObject requestData) {
        if(requestData != null && requestData.get("pk") != null){
            Integer pk = microsoftAzureMapper.deleteByPk(requestData.get("pk").toString());
            if(pk>0){
                return true;
            }else{
                //影响行数为0
                return false;
            }
        }else{
            return false;
        }
    }

    @Transactional
    public Boolean updateCloudTarget(JSONObject requestData) {
        if(requestData != null && requestData.get("pk") != null){
            Integer pk = microsoftAzureMapper.updateCloudTarget(requestData.get("pk").toString(),requestData);
            if(pk>0){
                return true;
            }else{
                //影响行数为0
                return false;
            }
        }else{
            return false;
        }
    }

    public DeviceCloudTargetPO getCloudTargetByPk(JSONObject requestData) {
        DeviceCloudTargetPO data = microsoftAzureMapper.getCloudTargetByPk(requestData.get("pk").toString());
        if(data != null){
            return data;
        }else{
            return new DeviceCloudTargetPO();
        }
    }

    public TypeAllVO imageCategories(Boolean isAll) {
        if (Objects.isNull(isAll)){
            isAll = Boolean.FALSE;
        }
        TypeAllConvertModel roles = typeConverter.findRoles(isAll);
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type(isAll);
        TypeAllConvertModel imageType = typeConverter.findImageType(isAll);
        TypeAllConvertModel diskFormat = typeConverter.findDiskFormat();
        TypeAllVO allResponse = new TypeAllVO();
        allResponse.setRole(Objects.nonNull(roles) ? roles.getIdIntegerModelList() : null);
        var firstTypeList = Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getFirstTypeList() : null;
        if (CollectionUtils.isNotEmpty(firstTypeList)) {
            for (int i = 0; i < firstTypeList.size(); i++) {
                if (firstTypeList.get(i).getId() == 5) {
                    firstTypeList.remove(i);
                    break;
                }
            }
        }
        allResponse.setFirstTypeList(firstTypeList);
        allResponse.setSecondTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getSecondTypeList() : null);
        allResponse.setThirdTypeList(Objects.nonNull(l1AndL2AndL3Type) ? l1AndL2AndL3Type.getThirdTypeList() : null);
        allResponse.setImageType(Objects.nonNull(imageType) ? imageType.getIdStringModelList() : null);
        allResponse.setHwDiskBusList(typeConverter.findHwDiskBus());
        allResponse.setHwVifModelList(typeConverter.findHwVifModel());
        allResponse.setHwVideoModelList(typeConverter.findHwVideoModel());
        allResponse.setDiskFormat(Objects.nonNull(diskFormat) ? diskFormat.getIdStringModelList() : null);
        return allResponse;
    }

    /**
     * 查询所有镜像管理分类
     */
    public List getImageCategories(){
        //查询所有数据
        String parentCode = ParentCodeEnum.PARENT_MIXCLOUD_CATEGORY_TYPE.getCode();
        List<MpSysCodeEnum> data = microsoftAzureMapper.getAlldata(parentCode);
        //根据parentCode字段将data数据按照父子关系处理返回新的List<MpSysCodeEnum>数据
        List<MpSysCodeEnum> dtoTreeList = getDtoTreeList(data, parentCode);
        return dtoTreeList;
    }

    /**
     * 将普通的entity的集合转成存在树状结构的集合
     * @param list
     * @return
     */
    public static List<MpSysCodeEnum> getDtoTreeList(List<MpSysCodeEnum> list, String parentCode){
        List<MpSysCodeEnum> dtos = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            return dtos;
        }
        for (MpSysCodeEnum menu : list) {
            if (parentCode.equals(menu.getParentCode())){
                //找到顶层目录
                MpSysCodeEnum menuDto = new MpSysCodeEnum();
                //将实体类的数据转成dto
                BeanUtils.copyProperties(menu,menuDto);
                //获取该实体类下的所有子级
                dtos.add(findChildren(menuDto,list));
            }
        }
        return dtos;
    }

    /**
     * 获取dto类下面的所有子级,存到dto中的children中
     * @param menuDto
     * @param list
     * @return
     */
    private static MpSysCodeEnum findChildren(MpSysCodeEnum menuDto, List<MpSysCodeEnum> list) {
        for (MpSysCodeEnum menu : list) {
            if (menuDto.getCode().equals(menu.getParentCode())){
                //此时menu是menuDto下的子级
                if (menuDto.getChildren() == null){
                    menuDto.setChildren(new ArrayList<MpSysCodeEnum>());
                }
                MpSysCodeEnum dto = new MpSysCodeEnum();
                BeanUtils.copyProperties(menu,dto);
                //递归
                menuDto.getChildren().add(findChildren(dto,list));
            }
        }
        return menuDto;
    }

    /**
     * 查询所有信息(带过滤条件)--操作系统、应用系统、网络设备、安全设备内靶标
     */
    public List getTarget(TargetSceneRequest request) {
        TypeAllConvertModel l1AndL2AndL3Type = typeConverter.findL1AndL2AndL3Type();
        List<TypeConvertModel> firstTypeList = l1AndL2AndL3Type.getFirstTypeList();
        List<TypeConvertModel> secondTypeList = l1AndL2AndL3Type.getSecondTypeList();

        Integer category = request.getCategory();
        if (Objects.nonNull(category)) {
            firstTypeList = firstTypeList.stream().filter(v -> v.getId().equals(category)).collect(Collectors.toList());
        }

        if (CollectionUtils.isNotEmpty(request.getSecondCategoryList())) {
            secondTypeList = secondTypeList.stream().filter(v -> request.getSecondCategoryList().contains(v.getId())).collect(Collectors.toList());
        }

        List<Device> deviceList = new ArrayList<>();
        for (TypeConvertModel first : firstTypeList){
            Integer firstId = first.getId();
            List<TypeConvertModel> typeConvertModels = secondTypeList.stream()
                    .filter(v -> v.getL1ParentId().equals(firstId)).collect(Collectors.toList());
            List<Integer> secondCategories = typeConvertModels.stream()
                    .map(model -> model.getId())
                    .collect(Collectors.toList());
            request.setCategory(firstId);
            request.setSecondCategories(secondCategories);
            request.setPage(null);
            request.setPerPage(null);
            BasePageBO<Device> targetSceneConvertModel = targetConverter.allTarget(new ImagePageDTO(request));
            if (Objects.isNull(targetSceneConvertModel) || CollectionUtils.isEmpty(targetSceneConvertModel.getList())) {
                continue;
            }
            List<Device> secondDeviceList = targetSceneConvertModel.getList();
            deviceList.addAll(secondDeviceList);
        }
        return deviceList;
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
