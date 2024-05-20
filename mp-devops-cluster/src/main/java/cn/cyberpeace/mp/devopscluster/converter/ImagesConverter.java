package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
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

    /**
     * 分页查询镜像列表
     * @param dto
     * @return
     */
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
        for (DeviceTargetPO d1 : deviceTargetPOS) {
            supportExports.put(d1.getUuid(), d1);
        }
        Map<String, Integer> orderMap = new HashMap<String, Integer>();
        if (CollectionUtils.isNotEmpty(deviceTargetPOS)) {
            for (int i=0; i<deviceTargetPOS.size(); i++) {
                orderMap.put(deviceTargetPOS.get(i).getUuid(), i);
            }
        }

        List<Integer> ids = new ArrayList<>();
        for (DeviceTargetPO po : deviceTargetPOS) {
            String id = po.getId();
            Integer parseInt = Integer.parseInt(id);
            ids.add(parseInt);
        }
        if (CollectionUtils.isEmpty(ids)){
            imageConvertPageModel.setList(Lists.newArrayList());
            return imageConvertPageModel;
        }
        List<Device> deviceList = this.findByIds(ids);
        if (CollectionUtils.isNotEmpty(deviceList)) {
            List<Device> list = new ArrayList<>();
            for (Device device : deviceList) {
                list.add(device);
            }
            list.sort((o1, o2) -> {
                if (orderMap.get(o1.getUuid()) > orderMap.get(o2.getUuid())) {
                    return 1;
                } else {
                    return -1;
                }
            });
            deviceList = list;
        }
        POLogoUtils.changeLogoPathToUrl(deviceList);

        if (CollectionUtils.isEmpty(deviceList)){
            imageConvertPageModel.setList(Lists.newArrayList());
            return imageConvertPageModel;
        }

        List<String> targetUuids = new ArrayList<>();
        for (DeviceTargetPO targetPO : deviceTargetPOS) {
            String uuid = targetPO.getUuid();
            targetUuids.add(uuid);
        }

        List<ImageAuthTotalCount> imageAuthTotalCounts = devopsTargetAuthService.totalCount(targetUuids);

        Map<String, Integer> imageAuthTotalCountsMap = new HashMap<>();
        for (ImageAuthTotalCount imageAuthTotalCount : imageAuthTotalCounts) {
            if (imageAuthTotalCountsMap.put(imageAuthTotalCount.getTarget(), imageAuthTotalCount.getTotal()) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }

        Map<String, PublicEnum> targetMap = new HashMap<>();
        for (DeviceTargetPO deviceTargetPO : deviceTargetPOS) {
            if (targetMap.put(deviceTargetPO.getUuid(), deviceTargetPO.getIsPublic()) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }

        Map<String, DeviceTargetPO> deviceTargetPOMap = new HashMap<>();
        for (DeviceTargetPO d : deviceTargetPOS) {
            if (deviceTargetPOMap.put(d.getUuid(), d) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }

        List<ImageConvertModel> modelList = new ArrayList<>();
        for (Device device : deviceList) {
            ImageConvertModel convertModel = wrapperImageConvertModel(device);
            modelList.add(convertModel);
        }

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

    /**
     * 新增镜像
     * @param request
     * @return
     */
    @Transactional
    public void add(ImageRequest request) {
        request.setModelId(TextUtils.uuid());
        request.setModelCode(TextUtils.uuid());
        Device device = imagesService.add(convertRequest(request));
        device.setIsHide(Objects.nonNull(request.getIsHide()) ? request.getIsHide() : Boolean.FALSE);
        device.setOsVersion(request.getOsVersion());
        device.setImagePath(request.getImagePath());
        device.setNetIfCnt(request.getNetIfCnt());
        device.setBrand(request.getBrand());
        device.setInnerApps(request.getInnerApps());
        devopsTargetService.insert(Collections.singletonList(device));
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
        List<ImageTargetConvertModel> list = new ArrayList<>();
        for (DeviceTargetPO v : deviceTargetPOList) {
            ImageTargetConvertModel imageTargetConvertModel = getImageTargetConvertModel(v);
            list.add(imageTargetConvertModel);
        }
        return list;
    }

    private static ImageTargetConvertModel getImageTargetConvertModel(DeviceTargetPO v) {
        ImageTargetConvertModel model = new ImageTargetConvertModel();
        model.setUuid(v.getUuid());
        model.setName(v.getName());
        return model;
    }

    /**
     * 更新镜像数据
     * @param request
     * @return
     */
    @Transactional
    public void update(ImageRequest request) {
        Device device = imagesService.update(request.getUuid(),convertRequest(request));
        devopsTargetService.updateById(device);
    }

    /**
     * 快速构建镜像
     * @param request
     * @return
     */
    public void build(ImagesBuildRequest request) {
        DeviceQuickCreateCloudPlatformModel model = new DeviceQuickCreateCloudPlatformModel(request.getName(),request.getImageName());
        List<InstallerDTO> installerDTOList = request.getInstallerDTOList();
        List<QuickCreateParam> quickCreateParams = new ArrayList<>();
        for (InstallerDTO v : installerDTOList) {
            QuickCreateParam quickCreateParam = getQuickCreateParam(v);
            quickCreateParams.add(quickCreateParam);
        }
        model.setQuickCreateParams(quickCreateParams);
        Device device = imagesService.quickCreate(model);
        devopsTargetService.insert(Collections.singletonList(device));
    }

    private static QuickCreateParam getQuickCreateParam(InstallerDTO v) {
        QuickCreateParam params = new QuickCreateParam();
        params.setInstallerUuid(v.getUuid());
        params.setSettings(v.getDockerInstallerData().getStartUpParameterList());
        params.setFile(v.getDockerInstallerData().getFile());
        return params;
    }

    /**
     * 删除镜像数据
     * @param request
     * @return
     */
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
        for (String s : imageIds) {
            DeviceTargetPO deviceTargetPO = new DeviceTargetPO();
            deviceTargetPO.setIsPublic(isAllPublic);
            deviceTargetPO.setUuid(s);
            deviceTargetMapper.updateById(deviceTargetPO);
        }
        //再新增权限数据
        List<String> userIds = request.getUserIds();
        if (PublicEnum.PRIVATE == isAllPublic){
            UserModel user = UserUtils.getLoginUser();
            if (Objects.nonNull(user)) {
                userIds.add(user.getId());
            }
        }
        if (PublicEnum.ALL_PUBLIC != isAllPublic && CollectionUtils.isNotEmpty(userIds)){
            for (String imageId : imageIds) {
                List<DeviceTargetAuthPO> authModelList = new ArrayList<>();
                for (String userId : userIds) {
                    DeviceTargetAuthPO deviceTargetAuthPO = getDeviceTargetAuthPO(imageId, userId);
                    authModelList.add(deviceTargetAuthPO);
                }
                devopsTargetAuthService.bulkCreate(authModelList);
            }
        }

    }

    private static DeviceTargetAuthPO getDeviceTargetAuthPO(String imageId, String userId) {
        DeviceTargetAuthPO deviceTargetAuthPO = new DeviceTargetAuthPO();
        deviceTargetAuthPO.setOwner(userId);
        deviceTargetAuthPO.setTarget(imageId);
        return deviceTargetAuthPO;
    }

    /**
     * 根据镜像ID查询用户列表
     * @param uuid
     * @return
     */
    public List<UserBo> findUsersByUuid(String uuid){
        List<DeviceTargetAuthPO> deviceTargetAuthPOS = devopsTargetAuthService.findByTarget(uuid);
        if (CollectionUtils.isEmpty(deviceTargetAuthPOS)){
            return Lists.newArrayList();
        }
        List<String> userIds = new ArrayList<>();
        for (DeviceTargetAuthPO deviceTargetAuthPO : deviceTargetAuthPOS) {
            String owner = deviceTargetAuthPO.getOwner();
            userIds.add(owner);
        }
        List<String> list = new ArrayList<>();
        for (String userId : userIds) {
            list.add(userId);
        }
        return userService.listByUserIds(list.toArray(new String[0]));
    }

    /**
     * 重启镜像机器
     * @param request
     * @return
     */
    public void reboot(ImagesRebootInstanceRequest request) {
        if (StringUtils.isBlank(request.getInstanceId()) || StringUtils.isBlank(request.getImageType())){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        if(ImageTypeEnum.VM.toValue().equals(request.getImageType())) {
            vmService.action("reboot", request.getInstanceId());
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

    /**
     * 导出镜像信息
     * @param uuid
     * @param imageName
     * @return
     */
    public String export(String uuid, String imageName) {
        return imagesService.export(uuid,imageName);
    }

    /**
     * 创建镜像机器
     * @param uuid
     */
    public void create(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        String insctanceCreateUser = Objects.nonNull(UserUtils.getLoginUser()) ? UserUtils.getLoginUser().getId() : StringUtils.EMPTY;
        scenariosService.create(new ImagesCreateInstanceCloudPlatform(uuid, insctanceCreateUser));
        DeviceTargetPO updateTargetModel = new DeviceTargetPO();
        updateTargetModel.setUuid(uuid);
        updateTargetModel.setInstanceCreateUser(insctanceCreateUser);
        deviceTargetMapper.updateById(updateTargetModel);
    }

    /**
     * 删除镜像机器
     * @param request
     */
    public void delete(ImagesDeleteInstanceRequest request) {
        if (StringUtils.isBlank(request.getUuid()) || StringUtils.isBlank(request.getImageType())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        scenariosService.delete(new ImagesDeleteInstanceCloudPlatform(request.getUuid(), request.getImageType()));
        DeviceTargetPO updateTargetModel = new DeviceTargetPO();
        updateTargetModel.setUuid(request.getUuid());
        updateTargetModel.setInstanceCreateUser(StringUtils.EMPTY);
        deviceTargetMapper.updateById(updateTargetModel);
    }

    /**
     * 根据ID集合（非UUID）查询靶标数据
     * @param ids
     */
    public List<Device> findByIds(List<Integer> ids){
        return deviceService.findByIds(ids);
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
        DeviceAsyncBO bo = targetConverter.getAsyncDeviceIds(requestIds);
        List<Integer> notExistedList = bo.getNotExistedList();
        List<Integer> existedList = bo.getExistedList();
        if (CollectionUtils.isEmpty(notExistedList) && CollectionUtils.isEmpty(existedList)){
            return requestIds.size();
        }
        Integer result = NumberUtils.INTEGER_ZERO;
        if (CollectionUtils.isNotEmpty(notExistedList)) {
            List<Device> deviceList = deviceService.findByIds(notExistedList);
            if (CollectionUtils.isNotEmpty(deviceList)) {
                for (Device device : deviceList) {
                    device.setIsHide(Objects.nonNull(isHide) ? isHide : Boolean.FALSE);
                }
                result += devopsTargetService.insert(deviceList);
            }
        }
        if (CollectionUtils.isNotEmpty(existedList)){
            List<Device> deviceList = deviceService.findByIds(existedList);
            if (CollectionUtils.isNotEmpty(deviceList)) {
                for (Device device : deviceList) {
                    devopsTargetService.updateById(device);
                }
                result+= existedList.size();
            }
        }
        return result;
    }


    /**
     * 转换ImageRequest
     * @param request
     * @return
     */
    protected Device convertRequest(ImageRequest request){
        Device device = new Device();
        device.setName(request.getName());
        device.setDeviceVersion(request.getDeviceVersion());
        device.setParentName(request.getParentName());
        device.setDescription(request.getDescription());
        device.setLogo(DefaultImgUtil.fdfsUrlToPath(request.getLogo()));
        Integer role  = request.getRole();
        device.setRole(role);
        device.setRoleId(request.getRoleId());
        device.setCategory(request.getCategory());
        device.setSecondCategory(request.getSecondCategory());
        device.setL3Category(request.getThirdCategory());
        device.setImageType(request.getImageType());
        device.setMaintype(MainTypeEnum.NORMAL.ordinal());
        device.setRootPassword(request.getRootPassword());
        device.setUsername(request.getUsername());
        final ExtVm extVm = request.getExtVm();
        if (Objects.nonNull(extVm) && CollectionUtils.isNotEmpty(request.getAccessProtocolList())) {
            extVm.setAccessProtocols(request.getAccessProtocolList());
        }
        device.setExtSystemInfo(request.getExtSystemInfo());
        ExtImageInfo extImageInfo = request.getExtImageInfo();
        if (Objects.nonNull(extImageInfo) && StringUtils.isNotBlank(extImageInfo.getUploadType())
                && extImageInfo.getUploadType().equals(UploadTypeEnum.FTP.getDesc())){
            extImageInfo.setUserFilename(extImageInfo.getFtpFilename());
            extImageInfo.setFilename(extImageInfo.getFtpFilename());
        } else {
            extImageInfo.setFilename(extImageInfo.getUserFilename());
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
        device.setModelId(request.getModelId());
        device.setModelCode(request.getModelCode());
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
