package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.ExtDocker;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.ExtImageInfo;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.ExtVm;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.*;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageConvertPageModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageTargetConvertModel;
import cn.cyberpeace.mp.devops.cluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.enums.ImageTypeEnum;
import cn.cyberpeace.mp.devops.cluster.enums.MainTypeEnum;
import cn.cyberpeace.mp.devops.cluster.enums.PublicEnum;
import cn.cyberpeace.mp.devops.cluster.enums.UploadTypeEnum;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.repository.ImageAuthMapper;
import cn.cyberpeace.mp.devops.cluster.repository.ImageMapper;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.service.DevopsTargetService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ImagesConverter implements BaseConverter{

    private ImagesService imagesService;

    private ImageMapper imageMapper;

    private ImageAuthMapper imageAuthMapper;

    private DeviceService deviceService;

    private VmService vmService;

    private ContainerService containerService;

    private DevopsTargetService devopsTargetService;

    private UserService userService;

    private ScenariosService scenariosService;

    private TargetConverter targetConverter;

    @Autowired
    public ImagesConverter(ImagesService imagesService, ImageMapper imageMapper,
                           DeviceService deviceService, ImageAuthMapper imageAuthMapper,
                           VmService vmService,ContainerService containerService,
                               DevopsTargetService devopsTargetService,UserService userService,
                               ScenariosService scenariosService, TargetConverter targetConverter) {
        this.imagesService = imagesService;
        this.imageMapper = imageMapper;
        this.deviceService = deviceService;
        this.imageAuthMapper = imageAuthMapper;
        this.vmService = vmService;
        this.containerService = containerService;
        this.devopsTargetService = devopsTargetService;
        this.userService = userService;
        this.scenariosService = scenariosService;
        this.targetConverter = targetConverter;
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(BasePageRequest::getPage,BasePageRequest::getPerPage);
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(BasePageRequest::getLimit,BasePageRequest::getOffset,
                BasePageRequest::getPage,BasePageRequest::getPerPage);
    }

    /**
     * 分页查询镜像列表
     * @param request
     * @return
     */
    public ImageConvertPageModel page(ImagePageRequest request) {
        ImagePageDTO dto = convertImagePageRequest(request);
        Integer total = devopsTargetService.totalCount(dto);

        ImageConvertPageModel imageConvertPageModel = new ImageConvertPageModel();
        if (total == 0){
            imageConvertPageModel.setTotal(0);
            imageConvertPageModel.setModelList(Lists.newArrayList());
            return imageConvertPageModel;
        }
        List<TargetModel> targetModels = imageMapper.page(dto);

        List<Integer> ids = targetModels.stream().map(TargetModel::getId).map(Integer::parseInt).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)){
            imageConvertPageModel.setTotal(0);
            imageConvertPageModel.setModelList(Lists.newArrayList());
            return imageConvertPageModel;
        }
        DeviceCloudPlatformModel model = deviceService.findByIds(ids);

        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getItems())){
            imageConvertPageModel.setTotal(0);
            imageConvertPageModel.setModelList(Lists.newArrayList());
            return imageConvertPageModel;
        }

        List<String> targetUuids = targetModels.stream().map(TargetModel::getUuid).collect(Collectors.toList());

        List<ImageAuthTotalCount> imageAuthTotalCounts = imageAuthMapper.totalCount(targetUuids);

        Map<String, Integer> imageAuthTotalCountsMap = imageAuthTotalCounts.stream().collect(Collectors.toMap(ImageAuthTotalCount::getTarget, ImageAuthTotalCount::getTotal));

        Map<String, PublicEnum> targetMap = targetModels.stream().collect(Collectors.toMap(TargetModel::getUuid, TargetModel::getIsPublic));

        List<ImageConvertModel> modelList = model.getItems().stream().map(this::wrapperImageConvertModel).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(modelList)) {
            for (ImageConvertModel imageConvertModel : modelList) {
                if (MapUtils.isNotEmpty(targetMap)) {
                    imageConvertModel.setIsPublic(targetMap.get(imageConvertModel.getUuid()));
                }
                imageConvertModel.setAuthTotal(imageAuthTotalCountsMap.get(imageConvertModel.getUuid()));
            }
        }

        imageConvertPageModel.setModelList(modelList);
        imageConvertPageModel.setTotal(total);
        return imageConvertPageModel;
    }

    /**
     * 新增镜像
     * @param request
     * @return
     */
    @Transactional
    public Device add(ImageRequest request) {
        Device device = imagesService.add(convertRequest(request));
        if (Objects.isNull(device) || !device.isSuccess()){
            return device;
        }
        devopsTargetService.insert(Arrays.asList(device));
        return device;
    }

    /**
     * 根据ID查询镜像信息
     * @param id
     * @return
     */
    public ImageConvertModel findById(String id) {
        Device device = deviceService.findById(id);
        return wrapperImageConvertModel(device);
    }

    /**
     * 根据UUID集合查询镜像数据
     * @param uuids
     * @return
     */
    public List<ImageTargetConvertModel> findByUuids(List<String> uuids) {
        List<TargetModel> targetModelList = imageMapper.findByUuids(uuids);
        return targetModelList.stream().map(v -> {
            ImageTargetConvertModel model = new ImageTargetConvertModel();
            model.setUuid(v.getUuid());
            model.setName(v.getName());
            return model;
        }).collect(Collectors.toList());
    }

    /**
     * 更新镜像数据
     * @param request
     * @return
     */
    @Transactional
    public Device update(ImageRequest request) {
        Device device = imagesService.update(request.getUuid(),convertRequest(request));
        if (Objects.isNull(device) || !device.isSuccess()){
            return device;
        }
        devopsTargetService.updateById(device);
        return device;
    }

    /**
     * 快速构建镜像
     * @param request
     * @return
     */
    public Device build(ImagesBuildRequest request) {
        Device device =  imagesService.quickCreate(new DeviceQuickCreateCloudPlatformModel(request.getName(),request.getImageName(),request.getServiceIds()));
        devopsTargetService.insert(Arrays.asList(device));
        return device;
    }

    /**
     * 删除镜像数据
     * @param request
     * @return
     */
    @Transactional
    public Boolean delete(ImagesDeleteRequest request) {
        targetConverter.validTargetCanDelete(request.getImageIds());
        Boolean result = deviceService.deleteByIds(request.getImageIds());

        if (CollectionUtils.isNotEmpty(request.getImageIds())){
            ImageDeleteDTO dto = new ImageDeleteDTO();
            dto.setIds(request.getImageIds());
            imageMapper.delete(dto);
            imageAuthMapper.deleteByTargets(request.getImageIds());
        }
        return result;
    }

    /**
     * 授权镜像信息
     * @param request
     */
    @Transactional(rollbackFor = Exception.class)
    public void auth(ImagesAuthRequest request) {
        //镜像ID
        List<String> imageIds = request.getUuids();
        //先删除上次的权限数据
        imageAuthMapper.deleteByTargets(imageIds);

        PublicEnum isAllPublic = request.getIsAllPublic();
        imageIds.forEach(imageId -> {
            TargetModel targetModel = new TargetModel();
            targetModel.setIsPublic(isAllPublic);
            targetModel.setUuid(imageId);
            imageMapper.updateById(targetModel);
        });
        //再新增权限数据
        List<String> userIds = request.getUserIds();
        if (PublicEnum.PRIVATE == isAllPublic){
            userIds.add(UserUtils.getLoginUser().getId());
        }
        if (PublicEnum.ALL_PUBLIC != isAllPublic && CollectionUtils.isNotEmpty(userIds)){
            imageIds.forEach(imageId -> {
                List<TargetAuthModel> authModelList = userIds.stream().map(userId -> {
                    TargetAuthModel targetAuthModel = new TargetAuthModel();
                    targetAuthModel.setOwner(userId);
                    targetAuthModel.setTarget(imageId);
                    return targetAuthModel;
                }).collect(Collectors.toList());
                imageAuthMapper.bulkCreate(authModelList);
            });
        }

    }

    /**
     * 根据镜像ID查询用户列表
     * @param uuid
     * @return
     */
    public List<UserDto> findUsersByUuid(String uuid){
        List<TargetAuthModel> targetAuthModels = imageAuthMapper.findByTarget(uuid);
        if (CollectionUtils.isEmpty(targetAuthModels)){
            return Lists.newArrayList();
        }
        List<String> userIds = targetAuthModels.stream().map(TargetAuthModel::getOwner).collect(Collectors.toList());
        return userService.listByUserIds(userIds.stream().toArray(String[]::new));
    }

    /**
     * 重启镜像机器
     * @param request
     * @return
     */
    public BaseCloudPlatformModel reboot(ImagesRebootInstanceRequest request) {
        BaseCloudPlatformModel platformModel = null;
        if(ImageTypeEnum.VM.toValue().equals(request.getImageType())) {
            VmActionCloudPlatformModel model = new VmActionCloudPlatformModel();
            model.setReboot(new VmActionCloudPlatformModel.Reboot("HARD"));
            platformModel = vmService.action(model,request.getInstanceId());
        }
        else if(ImageTypeEnum.CONTAINER.toValue().equals(request.getImageType())) {
            ContainerActionCloudPlatformModel model = new ContainerActionCloudPlatformModel();
            model.setAction("reboot");
            model.setWaitForStatus(Boolean.TRUE);
            platformModel = containerService.action(model, request.getInstanceId());
        }
        return platformModel;
    }

    /**
     * 导出镜像信息
     * @param uuid
     * @param imageName
     * @return
     */
    public BaseCloudPlatformModel export(String uuid, String imageName) {
        return imagesService.export(uuid,imageName);
    }

    /**
     * 创建镜像机器
     * @param uuid
     * @return
     */
    public BaseCloudPlatformModel create(String uuid) {
        return scenariosService.create(new ImagesCreateInstanceCloudPlatform(uuid,
                Objects.nonNull(UserUtils.getLoginUser()) ? UserUtils.getLoginUser().getUsername() : ""));
    }

    /**
     * 删除镜像机器
     * @param request
     * @return
     */
    public BaseCloudPlatformModel delete(ImagesDeleteInstanceRequest request) {
        return scenariosService.delete(new ImagesDeleteInstanceCloudPlatform(request.getUuid(),request.getImageType()));
    }

    /**
     * 根据ID集合（非UUID）查询靶标数据
     * @param ids
     */
    public List<Device> findByIds(List<Integer> ids){
        DeviceCloudPlatformModel model = deviceService.findByIds(ids);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getItems())){
            return Lists.newArrayList();
        }
        return model.getItems();
    }

    /**
     * 同步镜像数据
     * @param request
     * @return
     */
    @Transactional
    public Integer async(ImageAsyncRequest request){
        List<Device> deviceList = this.findByIds(request.getIds());
        if (CollectionUtils.isEmpty(deviceList)){
            return 0;
        }
        return devopsTargetService.insert(deviceList);
    }

    /**
     * 转换ImageRequest
     * @param request
     * @return
     */
    protected Device convertRequest(ImageRequest request){
        Device device = new Device();
        device.setName(request.getName());
        device.setDescription(request.getDescription());
        device.setLogo(request.getLogo());
        Integer role  = request.getRole();
        device.setRole(role);
        device.setCategory(request.getCategory());
        device.setSecondCategory(request.getSecondCategory());
        device.setL3Category(request.getThirdCategory());
        device.setImageType(request.getImageType());
        device.setMaintype(MainTypeEnum.NORMAL.ordinal());
        final ExtVm extVm = request.getExtVm();
        if (Objects.nonNull(extVm) && CollectionUtils.isNotEmpty(request.getAccessProtocolList())) {
            extVm.setAccessProtocols(request.getAccessProtocolList());
        }
        device.setExtSystemInfo(request.getExtSystemInfo());
        ExtImageInfo extImageInfo = request.getExtImageInfo();
        if (Objects.nonNull(extImageInfo) && StringUtils.isNotBlank(extImageInfo.getUploadType())
                && extImageInfo.getUploadType().equals(UploadTypeEnum.FTP.getDesc())){
            extImageInfo.setUserFilename(extImageInfo.getFtpFilename());
        }
        //解决外部网络靶标-镜像状态问题
        if (StringUtils.isNotBlank(request.getImageType())) {
            device.setExtImageInfo(extImageInfo);
        }
        device.setExtGateway(request.getExtGateway());
        ExtDocker extDocker = request.getExtDocker();
        if (request.getImageType().equals("container") && Objects.nonNull(extDocker)
                && CollectionUtils.isNotEmpty(request.getAccessProtocolList())){
            extDocker.setAccessProtocols(request.getAccessProtocolList());
        }

        if (CollectionUtils.isEmpty(request.getAccessProtocolList())){
            if (Objects.nonNull(extVm)) {
                extVm.setAccessProtocols(Lists.newArrayList());
            }
            if (Objects.nonNull(extDocker)) {
                extDocker.setAccessProtocols(Lists.newArrayList());
            }
        }
        device.setExtVm(extVm);
        device.setExtDocker(extDocker);
        return device;
    }

    /**
     * 转换Device
     * @param device
     * @return
     */
    protected ImageConvertModel wrapperImageConvertModel(Device device){
        ImageConvertModel imageModel = new ImageConvertModel();
        imageModel.setUuid(device.getUuid());
        imageModel.setName(device.getName());
        imageModel.setLogo(device.getLogo());
        imageModel.setMainCategory(device.getCategory());
        imageModel.setSubCategory(device.getSecondCategory());
        imageModel.setRole(device.getRole());
        imageModel.setThirdCategory(device.getL3Category());
        imageModel.setImageType(device.getImageType());
        imageModel.setIsImport(device.getIsImport());
        imageModel.setIsInternal(device.getIsInternal());
        imageModel.setDescription(device.getDescription());
        imageModel.setExtSystemInfo(device.getExtSystemInfo());
        ExtImageInfo extImageInfo = device.getExtImageInfo();
        if (Objects.nonNull(extImageInfo) && StringUtils.isNotBlank(extImageInfo.getUploadType())
                && extImageInfo.getUploadType().equals(UploadTypeEnum.FTP.getDesc())){
            extImageInfo.setFtpFilename(extImageInfo.getUserFilename());
        }
        imageModel.setExtImageInfo(extImageInfo);
        imageModel.setExtVm(device.getExtVm());
        imageModel.setExtGateway(device.getExtGateway());
        imageModel.setExtDocker(device.getExtDocker());
        return imageModel;
    }

    /**
     * 转换ImagePageRequest
     * @param request
     * @return
     */
    protected ImagePageDTO convertImagePageRequest(ImagePageRequest request){
        ImagePageDTO dto = new ImagePageDTO();
        dto.setRole(request.getRole());
        dto.setMainCategory(request.getMainCategory());
        dto.setSubCategory(request.getSubCategory());
        dto.setThirdCategory(request.getThirdCategory());
        dto.setQuickCreateSupport(request.getQuickCreateSupport());
        if (Objects.isNull(request.getImageType())){
            dto.setSearchType(1);
        }else {
            dto.setImageType(request.getImageType());
        }
        dto.setName(request.getName());
        dto.setOwner(Objects.nonNull(UserUtils.getLoginUser()) ? UserUtils.getLoginUser().getId() : null);
        dto.setSearchPublicEnum(1);
        dto.setOffset((request.getPage() - 1) * request.getPerPage());
        dto.setLimit(request.getPerPage());
        dto.setSystemType(request.getSystemType());
        dto.setSystemArchitecure(request.getSystemArchitecure());
        dto.setLanguage(request.getLanguage());
        dto.setImageStatus(request.getImageStatus());
        dto.setBaseImageSupport(request.getBaseImageSupport());
        dto.setLikeName(request.getLikeName());
        return dto;
    }
}
