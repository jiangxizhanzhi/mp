package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.*;
import cn.cyberpeace.mp.devopscluster.converter.bo.DeviceAsyncBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.converter.model.ImageConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.ImageTargetConvertModel;
import cn.cyberpeace.mp.devopscluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.dto.InstallerDTO;
import cn.cyberpeace.mp.devopscluster.enums.ImageTypeEnum;
import cn.cyberpeace.mp.devopscluster.enums.MainTypeEnum;
import cn.cyberpeace.mp.devopscluster.enums.PublicEnum;
import cn.cyberpeace.mp.devopscluster.enums.UploadTypeEnum;
import cn.cyberpeace.mp.devopscluster.model.*;
import cn.cyberpeace.mp.devopscluster.model.common.ServiceStatus;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetAuthPO;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.request.*;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetAuthService;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetService;
import cn.cyberpeace.mp.devopscluster.utils.POLogoUtils;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudErrorCode;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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

    private final DynamipsService dynamipsService;

    private final DevopsTargetService devopsTargetService;

    private final UserService userService;

    private final ScenariosService scenariosService;

    private final TargetConverter targetConverter;

    private final SnapshotConverter snapshotConverter;

    
    public BasePageBO<ImageConvertModel> page(ImagePageDTO dto) {
        Integer total = devopsTargetService.totalCount(dto);

        BasePageBO<ImageConvertModel> imageConvertPageModel = new BasePageBO<>();
        imageConvertPageModel.setTotal(total);
        if (total == 0){
            imageConvertPageModel.setList(Lists.newArrayList());
            return imageConvertPageModel;
        }
        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.page(dto);
        Map<String,DeviceTargetPO> supportExports = new HashMap<>();
        deviceTargetPOS.stream().forEach(d -> {
            supportExports.put(d.getUuid(), d);
        });
        Map<String, Integer> orderMap = new HashMap<String, Integer>();
        if (CollectionUtils.isNotEmpty(deviceTargetPOS)) {
            for (int i=0; i<deviceTargetPOS.size(); i++) {
                orderMap.put(deviceTargetPOS.get(i).getUuid(), i);
            }
        }

        List<Integer> ids = deviceTargetPOS.stream().map(DeviceTargetPO::getId).map(Integer::parseInt).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)){
            imageConvertPageModel.setList(Lists.newArrayList());
            return imageConvertPageModel;
        }
        List<Device> deviceList = this.findByIds(ids);
        if (CollectionUtils.isNotEmpty(deviceList)) {
            deviceList = deviceList.stream().sorted((o1, o2) -> {
                if (orderMap.get(o1.getUuid()) > orderMap.get(o2.getUuid())) {
                    return 1;
                } else {
                    return -1;
                }
            }).collect(Collectors.toList());
        }
        POLogoUtils.changeLogoPathToUrl(deviceList);

        if (CollectionUtils.isEmpty(deviceList)){
            imageConvertPageModel.setList(Lists.newArrayList());
            return imageConvertPageModel;
        }

        List<String> targetUuids = deviceTargetPOS.stream().map(DeviceTargetPO::getUuid).collect(Collectors.toList());

        List<ImageAuthTotalCount> imageAuthTotalCounts = devopsTargetAuthService.totalCount(targetUuids);

        Map<String, Integer> imageAuthTotalCountsMap = imageAuthTotalCounts.stream().collect(Collectors.toMap(ImageAuthTotalCount::getTarget, ImageAuthTotalCount::getTotal));

        Map<String, PublicEnum> targetMap = deviceTargetPOS.stream().collect(Collectors.toMap(DeviceTargetPO::getUuid, DeviceTargetPO::getIsPublic));

        Map<String, DeviceTargetPO> deviceTargetPOMap = deviceTargetPOS.stream().collect(Collectors.toMap(DeviceTargetPO::getUuid, (d) -> d));

        List<ImageConvertModel> modelList = deviceList.stream().map(this::wrapperImageConvertModel).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(modelList)) {
            for (ImageConvertModel imageConvertModel : modelList) {
                if (MapUtils.isNotEmpty(targetMap)) {
                    imageConvertModel.setIsPublic(targetMap.get(imageConvertModel.getUuid()));
                }
                if (MapUtils.isNotEmpty(deviceTargetPOMap) && deviceTargetPOMap.containsKey(imageConvertModel.getUuid())) {
                    imageConvertModel.setCreateUserName(deviceTargetPOMap.get(imageConvertModel.getUuid()).getCreateUserName());
                }
                imageConvertModel.setAuthTotal(imageAuthTotalCountsMap.get(imageConvertModel.getUuid()));
                if (supportExports.containsKey(imageConvertModel.getUuid())) {
                    imageConvertModel.setSupportExport(supportExports.get(imageConvertModel.getUuid()).getSupportExport());
                    if (ImageTypeEnum.CONTAINER.toValue().equals(imageConvertModel.getImageType())) {
                        if (imageConvertModel.getExtDocker() != null) {
                            imageConvertModel.getExtDocker().setInitSupport(supportExports.get(imageConvertModel.getUuid()).getInitSupport());
                        }
                    }
                }
            }
        }

        imageConvertPageModel.setList(modelList);
        return imageConvertPageModel;
    }

    
    @Transactional
    public void add(ImageRequest request) {
        Device device = imagesService.add(convertRequest(request));
        device.setIsHide(Objects.nonNull(request.getIsHide()) ? request.getIsHide() : Boolean.FALSE);
        devopsTargetService.insert(Collections.singletonList(device));
    }

    
    public ImageConvertModel findByUuid(String id) {
        Device device = deviceService.findByUuid(id);
        return wrapperImageConvertModel(device);
    }

    
    public List<ImageTargetConvertModel> findByUuids(List<String> uuids) {
        List<DeviceTargetPO> deviceTargetPOList = deviceTargetMapper.findByUuids(uuids);
        return deviceTargetPOList.stream().map(v -> {
            ImageTargetConvertModel model = new ImageTargetConvertModel();
            model.setUuid(v.getUuid());
            model.setName(v.getName());
            return model;
        }).collect(Collectors.toList());
    }

    
    @Transactional
    public void update(ImageRequest request) {
        Device device = imagesService.update(request.getUuid(),convertRequest(request));
        devopsTargetService.updateById(device);
    }

    
    public void build(ImagesBuildRequest request) {
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
        devopsTargetService.insert(Collections.singletonList(device));
    }

    
    @Transactional
    public void delete(ImagesDeleteRequest request) {
        targetConverter.validTargetCanDelete(request.getImageIds());
        snapshotConverter.validSnapshot(request.getImageIds());
        BaseCloudPlatformResult result = deviceService.deleteByIds(request.getImageIds());

        if (CollectionUtils.isNotEmpty(request.getImageIds()) && result.isSuccess()){
            ImageDeleteDTO dto = new ImageDeleteDTO();
            dto.setIds(request.getImageIds());
            deviceTargetMapper.delete(dto);
            devopsTargetAuthService.deleteByTargets(request.getImageIds());
        }
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void auth(ImagesAuthRequest request) {
        
        List<String> imageIds = request.getUuids();
        
        devopsTargetAuthService.deleteByTargets(imageIds);

        PublicEnum isAllPublic = request.getIsAllPublic();
        imageIds.forEach(imageId -> {
            DeviceTargetPO deviceTargetPO = new DeviceTargetPO();
            deviceTargetPO.setIsPublic(isAllPublic);
            deviceTargetPO.setUuid(imageId);
            deviceTargetMapper.updateById(deviceTargetPO);
        });
        
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

    
    public List<UserBo> findUsersByUuid(String uuid){
        List<DeviceTargetAuthPO> deviceTargetAuthPOS = devopsTargetAuthService.findByTarget(uuid);
        if (CollectionUtils.isEmpty(deviceTargetAuthPOS)){
            return Lists.newArrayList();
        }
        List<String> userIds = deviceTargetAuthPOS.stream().map(DeviceTargetAuthPO::getOwner).collect(Collectors.toList());
        return userService.listByUserIds(userIds.stream().toArray(String[]::new));
    }

    
    public void reboot(ImagesRebootInstanceRequest request) {
        if (StringUtils.isBlank(request.getInstanceId()) || StringUtils.isBlank(request.getImageType())){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        if(ImageTypeEnum.VM.toValue().equals(request.getImageType())) {
            VmActionCloudPlatformModel model = new VmActionCloudPlatformModel();
            model.setReboot(new VmActionCloudPlatformModel.Reboot("HARD"));
            vmService.action(model,request.getInstanceId());
        }
        else if(ImageTypeEnum.CONTAINER.toValue().equals(request.getImageType())) {
            ContainerActionCloudPlatformModel model = new ContainerActionCloudPlatformModel();
            model.setAction("reboot");
            model.setWaitForStatus(Boolean.TRUE);
            containerService.action(model, request.getInstanceId());
        } else if (ImageTypeEnum.DYNAMIPS.toValue().equals(request.getImageType())) {
            throw new MpRuntimeException(CloudErrorCode.UN_SUPPORT_OPERATE);

        }
    }

    
    public String export(String uuid, String imageName) {
        return imagesService.export(uuid,imageName);
    }

    
    public void create(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        String insctanceCreateUser = Objects.nonNull(UserUtils.getLoginUser()) ? UserUtils.getLoginUser().getId() : StringUtils.EMPTY;
        scenariosService.create(new ImagesCreateInstanceCloudPlatform(uuid, insctanceCreateUser));
        var updateTargetModel = new DeviceTargetPO();
        updateTargetModel.setUuid(uuid);
        updateTargetModel.setInstanceCreateUser(insctanceCreateUser);
        deviceTargetMapper.updateById(updateTargetModel);
    }

    
    public void delete(ImagesDeleteInstanceRequest request) {
        if (StringUtils.isBlank(request.getUuid()) || StringUtils.isBlank(request.getImageType())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        scenariosService.delete(new ImagesDeleteInstanceCloudPlatform(request.getUuid(), request.getImageType()));
        var updateTargetModel = new DeviceTargetPO();
        updateTargetModel.setUuid(request.getUuid());
        updateTargetModel.setInstanceCreateUser(StringUtils.EMPTY);
        deviceTargetMapper.updateById(updateTargetModel);
    }

    
    public List<Device> findByIds(List<Integer> ids){
        CloudPlatformPageResult<Device> model = deviceService.findByIds(ids);
        return model.getList();
    }

    
    @Transactional
    public Integer async(ImageAsyncRequest request){
        List<Integer> requestIds = request.getIds();
        Boolean isHide = request.getIsHide();
        DeviceAsyncBO bo = targetConverter.getAsyncDeviceIds(requestIds);
        List<Integer> notExistedList = bo.getNotExistedList();
        List<Integer> existedList = bo.getExistedList();
        if (CollectionUtils.isEmpty(notExistedList) && CollectionUtils.isEmpty(existedList)){
            return requestIds.size();
        }
        Integer result = NumberUtils.INTEGER_ZERO;
        if (CollectionUtils.isNotEmpty(notExistedList)) {
            CloudPlatformPageResult<Device> model = deviceService.findByIds(notExistedList);
            List<Device> deviceList = model.getList();
            if (CollectionUtils.isNotEmpty(deviceList)) {
                deviceList.stream().forEach(device -> device.setIsHide(Objects.nonNull(isHide) ? isHide : Boolean.FALSE));
                result += devopsTargetService.insert(deviceList);
            }
        }
        if (CollectionUtils.isNotEmpty(existedList)){
            CloudPlatformPageResult<Device> model = deviceService.findByIds(existedList);
            List<Device> deviceList = model.getList();
            if (CollectionUtils.isNotEmpty(deviceList)) {
                deviceList.forEach(device -> devopsTargetService.updateById(device));
                result+= existedList.size();
            }
        }
        return result;
    }


    
    protected Device convertRequest(ImageRequest request){
        Device device = new Device();
        device.setName(request.getName());
        device.setDescription(request.getDescription());
        device.setLogo(DefaultImgUtil.fdfsUrlToPath(request.getLogo()));
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

    
    protected ImageConvertModel wrapperImageConvertModel(Device device){
        ImageConvertModel imageModel = new ImageConvertModel();
        imageModel.setUuid(device.getUuid());
        imageModel.setName(device.getName());
        imageModel.setLogo(DefaultImgUtil.pathToFdfsUrl(device.getLogo()));
        imageModel.setMainCategory(device.getCategory());
        imageModel.setSubCategory(device.getSecondCategory());
        imageModel.setRole(device.getRole());
        imageModel.setThirdCategory(device.getL3Category());
        imageModel.setImageType(device.getImageType());
        imageModel.setIsImport(device.getIsImport());
        imageModel.setIsInternal(device.getIsInternal());
        imageModel.setIsInit(device.getIsInit());
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

    
    public CloudPlatformPageResult<ServiceStatus> listServiceStatus() {
        CloudPlatformPageResult<ServiceStatus> serviceStatusModel = scenariosService.listServiceStatus();
        if (Objects.nonNull(serviceStatusModel) && CollectionUtils.isNotEmpty(serviceStatusModel.getList())) {
            return serviceStatusModel;
        } else {
            return new CloudPlatformPageResult<>(new ArrayList<>());
        }
    }

}
