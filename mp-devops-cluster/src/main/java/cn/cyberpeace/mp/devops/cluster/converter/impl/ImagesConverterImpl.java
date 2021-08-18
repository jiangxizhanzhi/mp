package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.devops.cluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devops.cluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.model.ImagesModel;
import cn.cyberpeace.mp.devops.cluster.repository.ImageMapper;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.service.DeviceService;
import cn.cyberpeace.mp.devops.cluster.service.ImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        imagePageDTO.setSystemType(request.getSystemType());
        List<ImagesModel> imagesModels = imageMapper.page(imagePageDTO);
        imagesService.page("");
    }

    @Override
    @Transactional
    public void add(ImageAddRequest request) {
//        imagesService.add();
        ImagesModel imagesModel = new ImagesModel();
        imagesModel.setName(request.getName());
        imagesModel.setRole(request.getRole());
        imagesModel.setMainCategory(request.getMainCategory());
        imagesModel.setSubCategory(request.getSubCategory());
        imagesModel.setType(request.getVirtualType());
        imageMapper.insert(imagesModel);
    }

    @Override
    public void update(ImageUpdateRequest request) {
//        imagesService.update();
        ImagesModel imagesModel = new ImagesModel();
        imagesModel.setId(request.getId());
        imagesModel.setName(request.getName());
        imagesModel.setRole(request.getRole());
        imagesModel.setMainCategory(request.getMainCategory());
        imagesModel.setSubCategory(request.getSubCategory());
        imagesModel.setType(request.getVirtualType());
        imageMapper.updateById(imagesModel);
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
}
