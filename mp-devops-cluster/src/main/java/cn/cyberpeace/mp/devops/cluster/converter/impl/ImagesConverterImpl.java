package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.LocalDateTimeUtils;
import cn.cyberpeace.mp.devops.cluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageAuthConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageConvertPageModel;
import cn.cyberpeace.mp.devops.cluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devops.cluster.enums.PublicEnum;
import cn.cyberpeace.mp.devops.cluster.enums.RoleEnum;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.ImageAuthModel;
import cn.cyberpeace.mp.devops.cluster.model.ImageModel;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtVm;
import cn.cyberpeace.mp.devops.cluster.repository.ImageAuthMapper;
import cn.cyberpeace.mp.devops.cluster.repository.ImageMapper;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.service.DeviceService;
import cn.cyberpeace.mp.devops.cluster.service.ImagesService;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImagesConverterImpl implements ImagesConverter {

    private ImagesService imagesService;

    private ImageMapper imageMapper;

    private ImageAuthMapper imageAuthMapper;

    private DeviceService deviceService;

    @Autowired
    public ImagesConverterImpl(ImagesService imagesService, ImageMapper imageMapper,DeviceService deviceService,
                               ImageAuthMapper imageAuthMapper) {
        this.imagesService = imagesService;
        this.imageMapper = imageMapper;
        this.deviceService = deviceService;
        this.imageAuthMapper = imageAuthMapper;
    }

    @Override
    public ImageConvertPageModel page(ImagePageRequest request) {

        HumanCondition outerCondition = new HumanCondition();
        if (Objects.nonNull(request.getImageType())) {
            outerCondition.filterEqual("image_type", request.getImageType());
        }
        if (StringUtils.isNotBlank(request.getName())) {
            outerCondition.filterContains("name", request.getName());
        }
        if (Objects.nonNull(request.getRole())) {
            outerCondition.filterEqual("role", request.getRole());
        }
        if (Objects.nonNull(request.getMainCategory())) {
            outerCondition.filterEqual("main_category", request.getMainCategory());
        }
        if (Objects.nonNull(request.getSubCategory())) {
            outerCondition.filterEqual("sub_category", request.getSubCategory());
        }
        outerCondition.pagination(request.getPage(), request.getPerPage());
        outerCondition.orderBy(ImageModel::getCreateTime);

        List<ImageModel> imageModels = imageAuthMapper.listTargetsByOwner("admin",outerCondition);

        List<String> ids = imageModels.stream().map(ImageModel::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        DeviceCloudPlatformModel model = deviceService.findByIds(ids);

        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getItems())){
            return null;
        }

        List<ImageConvertModel> modelList = model.getItems().stream().map(device -> wrapperImageConvertModel(device)).collect(Collectors.toList());

        ImageConvertPageModel convertModel = new ImageConvertPageModel();
        convertModel.setModelList(modelList);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    @Override
    @Transactional
    public Boolean add(ImageRequest request) {
//        UserModel loginUser = UserUtils.getLoginUser();
//        if (Objects.isNull(loginUser)){
//            log.error("============未登录，获取不到用户信息，新增镜像信息失败================");
//            return Boolean.FALSE;
//        }
        Device device = imagesService.add(convertRequest(request));
        if (Objects.nonNull(device) && device.isSuccess()) {

            ImageModel imageModel = convertDevice(device);
            imageModel.setIsPublic(PublicEnum.ALL_PUBLIC.toValue());
            imageMapper.insert(imageModel);

//            ImageAuthModel imageAuthModel = new ImageAuthModel();
//            imageAuthModel.setOwner("admin");
//            imageAuthModel.setTarget(device.getUuid());
//            imageAuthMapper.create(imageAuthModel);
        }
        return device.isSuccess();
    }

    @Override
    public ImageConvertModel findById(String id) {
        Device device = deviceService.findById(id);
        return wrapperImageConvertModel(device);
    }

    @Transactional
    @Override
    public void update(ImageRequest request) {
        Device device = imagesService.update(request.getUuid(),convertRequest(request));
        if (Objects.nonNull(device) && device.isSuccess()) {
            ImageModel oldImageModel = imageMapper.get(request.getUuid());
            ImageModel imageModel = convertDevice(device);
            imageModel.setIsPublic(oldImageModel.getIsPublic());
            imageMapper.updateById(imageModel);
        }
    }

    @Override
    public void look(ImagesLookRequest request) {
    }

    @Override
    public void build(ImagesBuildRequest request) {

    }

    @Override
    public ImageAuthConvertModel findAuthById(String imageId){
        ImageModel imageModel = imageMapper.get(imageId);
        ImageAuthConvertModel authConvertModel = new ImageAuthConvertModel();
        authConvertModel.setPublicEnum(PublicEnum.ALL_PUBLIC);
        if (Objects.nonNull(imageModel) && PublicEnum.ALL_PUBLIC.toValue() != imageModel.getIsPublic().intValue()){
            List<ImageAuthModel> authModelList = imageAuthMapper.list(new HumanCondition().filterEqual("target",imageId));
            List<String> userIds = authModelList.stream().map(ImageAuthModel::getOwner).collect(Collectors.toList());
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
        List<String> imageIds = request.getIds();
        //先删除上次的权限数据
        imageIds.forEach(imageId ->  imageAuthMapper.deleteByTarget(imageId));
        //再新增权限数据
        List<String> userIds = request.getUserIds();
        Boolean isAllPublic = request.getIsAllPublic();
        imageIds.forEach(imageId -> {
            ImageModel imageModel = new ImageModel();
            imageModel.setIsPublic(isAllPublic ? 1 : 0);
            imageModel.setId(imageId);
            imageMapper.updateById(imageModel);
        });
        if (!isAllPublic){
            if (CollectionUtils.isEmpty(userIds)){
                //私有的
                UserModel loginUser = UserUtils.getLoginUser();
                userIds.add(loginUser.getId());
            }
            imageIds.forEach(imageId -> {
                List<ImageAuthModel> authModelList = userIds.stream().map(userId -> {
                    ImageAuthModel imageAuthModel = new ImageAuthModel();
                    imageAuthModel.setOwner(userId);
                    imageAuthModel.setTarget(imageId);
                    return imageAuthModel;
                }).collect(Collectors.toList());
                imageAuthMapper.bulkCreate(authModelList);
            });
        }

    }

    @Override
    public void export(ImagesExportRequest request) {

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
        RoleEnum role = request.getRole();
        device.setRole(role.toValue());
        device.setCategory(request.getCategory());
        device.setSecondCategory(request.getSecondCategory());
        device.setMaintype(request.getVirtualType());
        if (role != RoleEnum.NETWORK) {
            device.setExtGateway(request.getExtGateway());
            final ExtVm extVm = request.getExtVm();
            if (Objects.nonNull(extVm) && CollectionUtils.isNotEmpty(request.getAccessProtocolList())) {
                extVm.setAccessProtocols(JSONArray.toJSONString(request.getAccessProtocolList()));
            }
            device.setExtVm(extVm);
            device.setExtSystemInfo(request.getExtSystemInfo());
            device.setExtImageInfo(request.getExtImageInfo());
        }
        return device;
    }

    /**
     * 转换Device
     * @param device
     * @return
     */
    protected ImageModel convertDevice(Device device){
        ImageModel imageModel = new ImageModel();
        imageModel.setId(device.getUuid());
        imageModel.setName(device.getName());
        imageModel.setRole(device.getRole());
        imageModel.setMainCategory(device.getCategory());
        imageModel.setSubCategory(device.getSecondCategory());
        imageModel.setImageType(device.getImageType());
        imageModel.setCreateTime(LocalDateTime.parse(device.getCreatedAt(), LocalDateTimeUtils.dateTimeFormatter));
        imageModel.setUpdateTime(LocalDateTime.parse(device.getUpdatedAt(), LocalDateTimeUtils.dateTimeFormatter));
        return imageModel;
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
        imageModel.setDescription(device.getDescription());
        imageModel.setExtSystemInfo(device.getExtSystemInfo());
        return imageModel;
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return new String[0];
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return new String[0];
    }
}
