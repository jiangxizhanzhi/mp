package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.LocalDateTimeUtils;
import cn.cyberpeace.mp.devops.cluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageAuthConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.ImageConvertPageModel;
import cn.cyberpeace.mp.devops.cluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devops.cluster.enums.ImageTypeEnum;
import cn.cyberpeace.mp.devops.cluster.enums.PublicEnum;
import cn.cyberpeace.mp.devops.cluster.enums.RoleEnum;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtVm;
import cn.cyberpeace.mp.devops.cluster.repository.ImageAuthMapper;
import cn.cyberpeace.mp.devops.cluster.repository.ImageMapper;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.service.ContainerService;
import cn.cyberpeace.mp.devops.cluster.service.DeviceService;
import cn.cyberpeace.mp.devops.cluster.service.ImagesService;
import cn.cyberpeace.mp.devops.cluster.service.VmService;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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

    private VmService vmService;

    private ContainerService containerService;

    @Autowired
    public ImagesConverterImpl(ImagesService imagesService, ImageMapper imageMapper,DeviceService deviceService,
                               ImageAuthMapper imageAuthMapper,VmService vmService,ContainerService containerService) {
        this.imagesService = imagesService;
        this.imageMapper = imageMapper;
        this.deviceService = deviceService;
        this.imageAuthMapper = imageAuthMapper;
        this.vmService = vmService;
        this.containerService = containerService;
    }

    @Override
    public ImageConvertPageModel page(ImagePageRequest request) {

        HumanCondition outerCondition = request.wrapperHumanCondition();

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
    public String upload(MultipartFile file) {
        try {
           return HttpUtils.doFormDataPost("http://47.102.192.61:9019/cpcloud/device/v1/upload/?module=device&type=vm",multipartFileToFile(file));
        } catch (Exception e) {
            log.error("上传文件失败，失败信息：{}",e.getMessage());
        }
        return null;
    }

    public static File multipartFileToFile(MultipartFile file) throws Exception {

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }

    //获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = ins.read(buffer, 0, 1024)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export(ImagesExportRequest request) {

    }

    @Override
    public void create(String uuid) {
        Device device = deviceService.findById(uuid);

        if(ImageTypeEnum.VM.getDesc().equals(device.getImageType())) {
            VmCreateCloudPlatformModel model = new VmCreateCloudPlatformModel();
            model.setName(device.getName());
            model.setImage(device.getName());
            vmService.create(model);
        }
        else if(ImageTypeEnum.CONTAINER.getDesc().equals(device.getImageType())) {
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
        imageModel.setImageType(device.getImageType());
        imageModel.setDescription(device.getDescription());
        imageModel.setExtSystemInfo(device.getExtSystemInfo());
        imageModel.setExtImageInfo(device.getExtImageInfo());
        imageModel.setExtVm(device.getExtVm());
        imageModel.setExtGateway(device.getExtGateway());
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
