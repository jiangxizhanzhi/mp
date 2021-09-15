package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageAuthConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageConvertPageModel;
import cn.cyberpeace.mp.devops.cluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.enums.ImageTypeEnum;
import cn.cyberpeace.mp.devops.cluster.enums.MainTypeEnum;
import cn.cyberpeace.mp.devops.cluster.enums.PublicEnum;
import cn.cyberpeace.mp.devops.cluster.enums.RoleEnum;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtVm;
import cn.cyberpeace.mp.devops.cluster.repository.ImageAuthMapper;
import cn.cyberpeace.mp.devops.cluster.repository.ImageMapper;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.service.*;
import cn.cyberpeace.mp.devops.cluster.utils.FileUtils;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImagesConverterImpl implements ImagesConverter {

    private ImagesService imagesService;

    private ImageMapper imageMapper;

    private ImageAuthMapper imageAuthMapper;

    private DeviceService deviceService;

    private VmService vmService;

    private ContainerService containerService;

    private DevopsTargetService devopsTargetService;


    @Autowired
    public ImagesConverterImpl(ImagesService imagesService, ImageMapper imageMapper,DeviceService deviceService,
                               ImageAuthMapper imageAuthMapper,VmService vmService,ContainerService containerService,
                               DevopsTargetService devopsTargetService) {
        this.imagesService = imagesService;
        this.imageMapper = imageMapper;
        this.deviceService = deviceService;
        this.imageAuthMapper = imageAuthMapper;
        this.vmService = vmService;
        this.containerService = containerService;
        this.devopsTargetService = devopsTargetService;
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return new String[0];
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return new String[0];
    }

    @Override
    public ImageConvertPageModel page(ImagePageRequest request) {

        ImagePageDTO dto = new ImagePageDTO();
        dto.setRole(request.getRole());
        dto.setMainCategory(request.getMainCategory());
        dto.setSubCategory(request.getSubCategory());
        dto.setQuickCreateSupport(request.getQuickCreateSupport());
        if (Objects.isNull(request.getImageType())){
            dto.setSearchType(1);
        }else {
            dto.setImageType(Objects.nonNull(request.getImageType()) ? request.getImageType().toValue() : "");
        }
        dto.setName(request.getName());
        dto.setOwner(Objects.nonNull(UserUtils.getLoginUser()) ? UserUtils.getLoginUser().getId() : null);
//        dto.setIsPublic(PublicEnum.ALL_PUBLIC);
        dto.setSearchPublicEnum(1);
        dto.setOffset((request.getPage() - 1) * request.getPerPage());
        dto.setLimit(request.getPerPage());

        Integer total = imageMapper.totalCount(dto);
        if (total == 0){
            return null;
        }
        List<TargetModel> targetModels = imageMapper.page(dto);

        List<Integer> ids = targetModels.stream().map(TargetModel::getId).map(Integer::parseInt).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        DeviceCloudPlatformModel model = deviceService.findByIds(ids);

        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getItems())){
            return null;
        }

        List<String> targetUuids = targetModels.stream().map(TargetModel::getUuid).collect(Collectors.toList());

        List<ImageAuthTotalCount> imageAuthTotalCounts = imageAuthMapper.totalCount(targetUuids);

        Map<String, Integer> imageAuthTotalCountsMap = imageAuthTotalCounts.stream().collect(Collectors.toMap(ImageAuthTotalCount::getTarget, ImageAuthTotalCount::getTotal));

        Map<String, PublicEnum> targetMap = targetModels.stream().collect(Collectors.toMap(TargetModel::getUuid, TargetModel::getIsPublic));

        List<ImageConvertModel> modelList = model.getItems().stream().map(device -> wrapperImageConvertModel(device)).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(modelList)) {
            for (ImageConvertModel imageConvertModel : modelList) {
                if (MapUtils.isNotEmpty(targetMap)) {
                    imageConvertModel.setIsPublic(targetMap.get(imageConvertModel.getUuid()));
                }
                imageConvertModel.setAuthTotal(imageAuthTotalCountsMap.get(imageConvertModel.getUuid()));
            }
        }

        ImageConvertPageModel convertModel = new ImageConvertPageModel();
        convertModel.setModelList(modelList);
        convertModel.setTotal(total);
        return convertModel;
    }

    @Override
    @Transactional
    public Boolean add(ImageRequest request) {
        Device device = imagesService.add(convertRequest(request));
        devopsTargetService.insert(device);
        return device.isSuccess();
    }

    @Override
    public ImageConvertModel findById(String id) {
        Device device = deviceService.findById(id);
        return wrapperImageConvertModel(device);
    }


    @Transactional
    @Override
    public Boolean update(ImageRequest request) {
        Device device = imagesService.update(request.getUuid(),convertRequest(request));
        devopsTargetService.updateById(device);
        return device.isSuccess();
    }

    @Override
    public void look(ImagesLookRequest request) {
    }

    @Override
    public void build(ImagesBuildRequest request) {
        imagesService.quickCreate(new DeviceQuickCreateCloudPlatformModel(request.getName(),request.getImageName(),request.getServiceIds()));
    }

    @Override
    public ImageAuthConvertModel findAuthById(String imageId){
        TargetModel targetModel = imageMapper.get(imageId);
        ImageAuthConvertModel authConvertModel = new ImageAuthConvertModel();
        authConvertModel.setPublicEnum(PublicEnum.ALL_PUBLIC);
        if (Objects.nonNull(targetModel) && PublicEnum.ALL_PUBLIC != targetModel.getIsPublic()){
            List<TargetAuthModel> authModelList = imageAuthMapper.list(new HumanCondition().filterEqual("target",imageId));
            List<String> userIds = authModelList.stream().map(TargetAuthModel::getOwner).collect(Collectors.toList());
            authConvertModel.setUserIds(userIds);
            authConvertModel.setPublicEnum(PublicEnum.CUSTOMIZE);

        }
        return authConvertModel;
    }

    @Override
    @Transactional
    public void delete(ImagesDeleteRequest request) {
        deviceService.deleteByIds(request.getImageIds());
        ImageDeleteDTO dto = new ImageDeleteDTO();
        dto.setIds(request.getImageIds());
        imageMapper.delete(dto);
        request.getImageIds().forEach(id -> imageAuthMapper.deleteByTarget(id));
    }

    @Transactional
    @Override
    public void auth(ImagesAuthRequest request) {
        //镜像ID
        List<String> imageIds = request.getUuids();
        //先删除上次的权限数据
        imageIds.forEach(imageId ->  imageAuthMapper.deleteByTarget(imageId));

        PublicEnum isAllPublic = request.getIsAllPublic();
        imageIds.forEach(imageId -> {
            TargetModel targetModel = new TargetModel();
            targetModel.setIsPublic(isAllPublic);
            targetModel.setUuid(imageId);
            imageMapper.updateById(targetModel);
        });
        //再新增权限数据
        List<String> userIds = request.getUserIds();
        if (PublicEnum.ALL_PUBLIC != isAllPublic && PublicEnum.PRIVATE != isAllPublic && CollectionUtils.isNotEmpty(userIds)){
//            if (CollectionUtils.isEmpty(userIds)){
//                //私有的
//                UserModel loginUser = UserUtils.getLoginUser();
//                userIds.add(loginUser.getId());
//            }
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

    @Override
    public String uploadImage(ImagesUploadRequest request, MultipartFile multipartFile) {
        if (Objects.isNull(multipartFile) || multipartFile.getSize() == 0 ){
            return null;
        }
        try {
            List<File> fileList = FileUtils.transferToFile(Arrays.asList(multipartFile));
            String params = BeanUtils.wrapperGetParamsByObject(Boolean.TRUE,request);
            return imagesService.upload(params,fileList.get(0));
        } catch (Exception e) {
            log.error("上传镜像文件失败：{}",e.getMessage());
        }

        return null;
    }



    @Override
    public HttpResponse export(String uuid) {
        return imagesService.export(uuid);
    }

    @Override
    public void create(String uuid) {
        Device device = deviceService.findById(uuid);

        if(ImageTypeEnum.VM.toValue().equals(device.getImageType())) {
            VmCreateCloudPlatformModel model = new VmCreateCloudPlatformModel();
            model.setName(device.getName());
            model.setImage(device.getName());
            vmService.create(model);
        }
        else if(ImageTypeEnum.CONTAINER.toValue().equals(device.getImageType())) {
            ContainerCreateCloudPlatformModel model = new ContainerCreateCloudPlatformModel();
            model.setName(device.getName());
            model.setImage(device.getName());
            containerService.create(model);
        }

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
        device.setImageType(request.getVirtualType());
        device.setMaintype(MainTypeEnum.NORMAL.ordinal());
        if (Objects.nonNull(role) && role != RoleEnum.NETWORK.toValue()) {
            final ExtVm extVm = request.getExtVm();
            if (Objects.nonNull(extVm) && CollectionUtils.isNotEmpty(request.getAccessProtocolList())) {
                extVm.setAccessProtocols(JSONArray.toJSONString(request.getAccessProtocolList()));
            }
            device.setExtVm(extVm);
            device.setExtSystemInfo(request.getExtSystemInfo());
            device.setExtImageInfo(request.getExtImageInfo());
            if (role == RoleEnum.GATEWAY.toValue()) {
                device.setExtGateway(request.getExtGateway());
            }
        }
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
        imageModel.setMainCategory(device.getCategory());
        imageModel.setSubCategory(device.getSecondCategory());
        imageModel.setImageType(device.getImageType());
        imageModel.setDescription(device.getDescription());
        imageModel.setExtSystemInfo(device.getExtSystemInfo());
        imageModel.setExtImageInfo(device.getExtImageInfo());
        imageModel.setExtVm(device.getExtVm());
        imageModel.setExtGateway(device.getExtGateway());
        return imageModel;
    }

}
