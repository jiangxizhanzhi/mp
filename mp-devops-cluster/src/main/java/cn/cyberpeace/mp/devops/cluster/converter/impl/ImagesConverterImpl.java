package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.util.LocalDateTimeUtils;
import cn.cyberpeace.mp.devops.cluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devops.cluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.enums.RoleEnum;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.ImageAuthModel;
import cn.cyberpeace.mp.devops.cluster.model.ImageModel;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtVm;
import cn.cyberpeace.mp.devops.cluster.repository.ImageAuthMapper;
import cn.cyberpeace.mp.devops.cluster.repository.ImageMapper;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.service.DeviceService;
import cn.cyberpeace.mp.devops.cluster.service.ImagesService;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    public void page(ImagePageRequest request) {
        ImagePageDTO imagePageDTO = new ImagePageDTO();
        imagePageDTO.setLimit(request.getPage());
        imagePageDTO.setOffset(request.getPerPage());
        imagePageDTO.setImageType(request.getImageType());
        imagePageDTO.setName(request.getName());
        imagePageDTO.setRole(request.getRole());
        imagePageDTO.setMainCategory(request.getMainCategory());
        imagePageDTO.setSubCategory(request.getSubCategory());
        List<ImageModel> imageModels = imageMapper.page(imagePageDTO);
        imagesService.page("");
    }

    @Override
    @Transactional
    public Boolean add(ImageRequest request) {
        Device device = imagesService.add(convertRequest(request));
        if (Objects.nonNull(device) && device.isSuccess()) {
            ImageModel imageModel = convertDevice(device);
            imageMapper.insert(imageModel);
            ImageAuthModel imageAuthModel = new ImageAuthModel();
            UserModel loginUser = UserUtils.getLoginUser();
            imageAuthModel.setOwner(Objects.nonNull(loginUser) ? loginUser.getId() : "admin");
            imageAuthModel.setTarget(device.getUuid());
            imageAuthMapper.create(imageAuthModel);
        }
        return device.isSuccess();
    }

    @Override
    public void update(ImageRequest request) {
        Device device = imagesService.update(request.getUuid(),convertRequest(request));
        if (Objects.nonNull(device) && device.isSuccess()) {
            ImageModel imageModel = convertDevice(device);
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
    @Transactional
    public void delete(ImagesDeleteRequest request) {
        deviceService.deleteByIds(request.getImageIds());
        ImageDeleteDTO dto = new ImageDeleteDTO();
        dto.setIds(request.getImageIds());
        imageMapper.delete(dto);
    }

    @Override
    public void auth(ImagesAuthRequest request) {

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
        imageModel.setType(device.getImageType());
        imageModel.setCreateTime(LocalDateTime.parse(device.getCreatedAt(), LocalDateTimeUtils.dateTimeFormatter));
        imageModel.setUpdateTime(LocalDateTime.parse(device.getUpdatedAt(), LocalDateTimeUtils.dateTimeFormatter));
        return imageModel;
    }
}
