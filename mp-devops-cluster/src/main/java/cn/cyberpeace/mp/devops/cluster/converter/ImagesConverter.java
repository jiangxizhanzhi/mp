package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.*;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageTargetConvertModel;
import cn.cyberpeace.mp.devops.cluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.dto.InstallerDTO;
import cn.cyberpeace.mp.devops.cluster.enums.ImageTypeEnum;
import cn.cyberpeace.mp.devops.cluster.enums.MainTypeEnum;
import cn.cyberpeace.mp.devops.cluster.enums.PublicEnum;
import cn.cyberpeace.mp.devops.cluster.enums.UploadTypeEnum;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.model.common.ServiceStatus;
import cn.cyberpeace.mp.devops.cluster.po.DeviceTargetAuthPO;
import cn.cyberpeace.mp.devops.cluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devops.cluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.service.DevopsTargetAuthService;
import cn.cyberpeace.mp.devops.cluster.service.DevopsTargetService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImagesConverter implements BaseConverter{

    private final ImagesService imagesService;

    private final DeviceTargetMapper deviceTargetMapper;

    private final DevopsTargetAuthService devopsTargetAuthService;

    private final DeviceService deviceService;

    private final VmService vmService;

    private final ContainerService containerService;

    private final DevopsTargetService devopsTargetService;

    private final UserService userService;

    private final ScenariosService scenariosService;

    private final TargetConverter targetConverter;

    /**
     * 分页查询镜像列表
     * @param dto
     * @return
     */
    public BasePageBO<ImageConvertModel> page(ImagePageDTO dto) {
        Integer total = devopsTargetService.totalCount(dto);

        BasePageBO<ImageConvertModel> imageConvertPageModel = new BasePageBO<>();
        if (total == 0){
            imageConvertPageModel.setTotal(0);
            imageConvertPageModel.setList(Lists.newArrayList());
            return imageConvertPageModel;
        }
        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.page(dto);

        List<Integer> ids = deviceTargetPOS.stream().map(DeviceTargetPO::getId).map(Integer::parseInt).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)){
            imageConvertPageModel.setTotal(0);
            imageConvertPageModel.setList(Lists.newArrayList());
            return imageConvertPageModel;
        }
        List<Device> deviceList = this.findByIds(ids);

        if (CollectionUtils.isEmpty(deviceList)){
            imageConvertPageModel.setTotal(0);
            imageConvertPageModel.setList(Lists.newArrayList());
            return imageConvertPageModel;
        }

        List<String> targetUuids = deviceTargetPOS.stream().map(DeviceTargetPO::getUuid).collect(Collectors.toList());

        List<ImageAuthTotalCount> imageAuthTotalCounts = devopsTargetAuthService.totalCount(targetUuids);

        Map<String, Integer> imageAuthTotalCountsMap = imageAuthTotalCounts.stream().collect(Collectors.toMap(ImageAuthTotalCount::getTarget, ImageAuthTotalCount::getTotal));

        Map<String, PublicEnum> targetMap = deviceTargetPOS.stream().collect(Collectors.toMap(DeviceTargetPO::getUuid, DeviceTargetPO::getIsPublic));

        List<ImageConvertModel> modelList = deviceList.stream().map(this::wrapperImageConvertModel).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(modelList)) {
            for (ImageConvertModel imageConvertModel : modelList) {
                if (MapUtils.isNotEmpty(targetMap)) {
                    imageConvertModel.setIsPublic(targetMap.get(imageConvertModel.getUuid()));
                }
                imageConvertModel.setAuthTotal(imageAuthTotalCountsMap.get(imageConvertModel.getUuid()));
            }
        }

        imageConvertPageModel.setList(modelList);
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
        device.setIsHide(Objects.nonNull(request.getIsHide()) ? request.getIsHide() : Boolean.FALSE);
        devopsTargetService.insert(Arrays.asList(device));
        return device;
    }

    /**
     * 根据ID查询镜像信息
     * @param id
     * @return
     */
    public ImageConvertModel findByUuid(String id) {
        Device device = deviceService.findByUuid(id);
        return wrapperImageConvertModel(device);
    }

    /**
     * 根据UUID集合查询镜像数据
     * @param uuids
     * @return
     */
    public List<ImageTargetConvertModel> findByUuids(List<String> uuids) {
        List<DeviceTargetPO> deviceTargetPOList = deviceTargetMapper.findByUuids(uuids);
        return deviceTargetPOList.stream().map(v -> {
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
        DeviceQuickCreateCloudPlatformModel model = new DeviceQuickCreateCloudPlatformModel(request.getName(),request.getImageName());
        List<InstallerDTO> installerDTOList = request.getInstallerDTOList();
        List<QuickCreateParam> quickCreateParams = installerDTOList.stream().map(v -> {
            QuickCreateParam params = new QuickCreateParam();
            params.setInstallerUuid(v.getUuid());
            params.setSettings(v.getDockerInstallerData().getStartUpParameterList());
            params.setFile(v.getDockerInstallerData().getFile());
            return params;
        }).collect(Collectors.toList());
        model.setQuickCreateParams(quickCreateParams);
        Device device = imagesService.quickCreate(model);
        devopsTargetService.insert(Arrays.asList(device));
        return device;
    }

    /**
     * 删除镜像数据
     * @param request
     * @return
     */
    @Transactional
    public BaseCloudPlatformResult delete(ImagesDeleteRequest request) {
        targetConverter.validTargetCanDelete(request.getImageIds());
        BaseCloudPlatformResult result = deviceService.deleteByIds(request.getImageIds());

        if (CollectionUtils.isNotEmpty(request.getImageIds()) && result.isSuccess()){
            ImageDeleteDTO dto = new ImageDeleteDTO();
            dto.setIds(request.getImageIds());
            deviceTargetMapper.delete(dto);
            devopsTargetAuthService.deleteByTargets(request.getImageIds());
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
        devopsTargetAuthService.deleteByTargets(imageIds);

        PublicEnum isAllPublic = request.getIsAllPublic();
        imageIds.forEach(imageId -> {
            DeviceTargetPO deviceTargetPO = new DeviceTargetPO();
            deviceTargetPO.setIsPublic(isAllPublic);
            deviceTargetPO.setUuid(imageId);
            deviceTargetMapper.updateById(deviceTargetPO);
        });
        //再新增权限数据
        List<String> userIds = request.getUserIds();
        if (PublicEnum.PRIVATE == isAllPublic){
            UserModel user = UserUtils.getLoginUser();
            if (Objects.nonNull(user)) {
                userIds.add(user.getId());
            }
        }
        if (PublicEnum.ALL_PUBLIC != isAllPublic && CollectionUtils.isNotEmpty(userIds)){
            imageIds.forEach(imageId -> {
                List<DeviceTargetAuthPO> authModelList = userIds.stream().map(userId -> {
                    DeviceTargetAuthPO deviceTargetAuthPO = new DeviceTargetAuthPO();
                    deviceTargetAuthPO.setOwner(userId);
                    deviceTargetAuthPO.setTarget(imageId);
                    return deviceTargetAuthPO;
                }).collect(Collectors.toList());
                devopsTargetAuthService.bulkCreate(authModelList);
            });
        }

    }

    /**
     * 根据镜像ID查询用户列表
     * @param uuid
     * @return
     */
    public List<UserDto> findUsersByUuid(String uuid){
        List<DeviceTargetAuthPO> deviceTargetAuthPOS = devopsTargetAuthService.findByTarget(uuid);
        if (CollectionUtils.isEmpty(deviceTargetAuthPOS)){
            return Lists.newArrayList();
        }
        List<String> userIds = deviceTargetAuthPOS.stream().map(DeviceTargetAuthPO::getOwner).collect(Collectors.toList());
        return userService.listByUserIds(userIds.stream().toArray(String[]::new));
    }

    /**
     * 重启镜像机器
     * @param request
     * @return
     */
    public BaseCloudPlatformResult reboot(ImagesRebootInstanceRequest request) {
        if (StringUtils.isBlank(request.getInstanceId()) || StringUtils.isBlank(request.getImageType())){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        BaseCloudPlatformResult platformModel = null;
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
    public BaseCloudPlatformResult export(String uuid, String imageName) {
        return imagesService.export(uuid,imageName);
    }

    /**
     * 创建镜像机器
     * @param uuid
     * @return
     */
    public BaseCloudPlatformResult create(String uuid) {
        if (StringUtils.isBlank(uuid)){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        return scenariosService.create(new ImagesCreateInstanceCloudPlatform(uuid,
                Objects.nonNull(UserUtils.getLoginUser()) ? UserUtils.getLoginUser().getUsername() : ""));
    }

    /**
     * 删除镜像机器
     * @param request
     * @return
     */
    public BaseCloudPlatformResult delete(ImagesDeleteInstanceRequest request) {
        if (StringUtils.isBlank(request.getUuid()) || StringUtils.isBlank(request.getImageType())){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        return scenariosService.delete(new ImagesDeleteInstanceCloudPlatform(request.getUuid(),request.getImageType()));
    }

    /**
     * 根据ID集合（非UUID）查询靶标数据
     * @param ids
     */
    public List<Device> findByIds(List<Integer> ids){
        CloudPlatformPageResult<Device> model = deviceService.findByIds(ids);
        return model.getList();
    }

    /**
     * 同步镜像数据
     * @param request
     * @return
     */
    @Transactional
    public Integer async(ImageAsyncRequest request){
        List<Integer> requestIds = request.getIds();
        Boolean isHide = request.getIsHide();
        List<Integer> diffList = targetConverter.getAsyncDeviceIds(requestIds);
        if (CollectionUtils.isEmpty(diffList)){
            return requestIds.size();
        }
        CloudPlatformPageResult<Device> model = deviceService.findByIds(diffList);
        List<Device> deviceList = model.getList();
        if (CollectionUtils.isEmpty(deviceList)){
            throw new MpRuntimeException(BaseErrorCode.DATA_NOT_EXIST);
        }
        deviceList.stream().forEach(device -> device.setIsHide(Objects.nonNull(isHide) ? isHide : Boolean.FALSE));
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
        device.setIsHide(Objects.nonNull(request.getIsHide()) ? request.getIsHide() : Boolean.FALSE );
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
     * 获取物理主机服务状态列表
     * @return
     */
    public CloudPlatformPageResult<ServiceStatus> listServiceStatus() {
        CloudPlatformPageResult<ServiceStatus> serviceStatusModel = scenariosService.listServiceStatus();
        if (Objects.nonNull(serviceStatusModel) && CollectionUtils.isNotEmpty(serviceStatusModel.getList())) {
            return serviceStatusModel;
        } else {
            return new CloudPlatformPageResult<>(new ArrayList<>());
        }
    }

}
