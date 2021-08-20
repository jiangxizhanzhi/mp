package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.devops.cluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devops.cluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.ImageModel;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtVm;
import cn.cyberpeace.mp.devops.cluster.repository.ImageMapper;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.service.DeviceService;
import cn.cyberpeace.mp.devops.cluster.service.ImagesService;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ImagesConverterImpl implements ImagesConverter {

    private ImagesService imagesService;

    private ImageMapper imageMapper;

    private DeviceService deviceService;

    @Autowired
    public ImagesConverterImpl(ImagesService imagesService, ImageMapper imageMapper,DeviceService deviceService) {
        this.imagesService = imagesService;
        this.imageMapper = imageMapper;
        this.deviceService = deviceService;
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
    public void add(ImageRequest request) {
        Device device = imagesService.add(convertRequest(request));
        ImageModel imageModel = new ImageModel();
        imageModel.setId(device.getUuid());
        imageModel.setName(request.getName());
        imageModel.setRole(request.getRole());
        imageModel.setMainCategory(request.getCategory());
        imageModel.setSubCategory(request.getSecondCategory());
        imageModel.setType(request.getVirtualType());
        imageMapper.insert(imageModel);
    }

    @Override
    public void update(ImageUpdateRequest request) {
//        imagesService.update();
        ImageModel imageModel = new ImageModel();
        imageModel.setId(request.getId());
        imageModel.setName(request.getName());
        imageModel.setRole(request.getRole());
        imageModel.setMainCategory(request.getMainCategory());
        imageModel.setSubCategory(request.getSubCategory());
        imageModel.setType(request.getVirtualType());
        imageMapper.updateById(imageModel);
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

    protected Device convertRequest(ImageRequest request){
        Device device = new Device();
        device.setName(request.getName());
        device.setDescription(request.getDescription());
        device.setLogo(request.getLogo());
        device.setRole(request.getRole());
        device.setCategory(request.getCategory());
        device.setSecondCategory(request.getSecondCategory());
        device.setMaintype(request.getVirtualType());
        device.setExtGateway(request.getExtGateway());
        final ExtVm extVm = request.getExtVm();
        if (Objects.nonNull(extVm) && CollectionUtils.isNotEmpty(request.getAccessProtocolList())){
            extVm.setAccessProtocols(JSONArray.toJSONString(request.getAccessProtocolList()));
        }
        device.setExtVm(extVm);

        device.setExtSystemInfo(request.getExtSystemInfo());
        device.setExtImageInfo(request.getExtImageInfo());
        return device;
    }
}
