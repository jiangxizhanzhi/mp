package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.devops.cluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.common.ExtEntity;
import cn.cyberpeace.mp.devops.cluster.converter.model.common.SystemInfo;
import cn.cyberpeace.mp.devops.cluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.ImagesModel;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtSystemInfo;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtVm;
import cn.cyberpeace.mp.devops.cluster.repository.ImageMapper;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.service.DeviceService;
import cn.cyberpeace.mp.devops.cluster.service.ImagesService;
import com.alibaba.fastjson.JSONArray;
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
        imagePageDTO.setSystemType(request.getSystemType());
        List<ImagesModel> imagesModels = imageMapper.page(imagePageDTO);
        imagesService.page("");
    }

    @Override
    @Transactional
    public void add(ImageRequest request) {
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

    protected Device convertRequest(ImageRequest request){
        Device device = new Device();
        if (Objects.isNull(request)){
            return null;
        }
//        device.setName(request.getName());
//        device.setDescription(request.getDescription());
//        device.setLogo(request.getLogo());
//        device.setRole(request.getRole());
//        device.setCategory(request.getMainCategory());
//        device.setSecond_category(request.getSubCategory());
//        device.setMaintype(request.getVirtualType());
//
//        final ExtVm extVm = request.getExtVm();
//        if (Objects.nonNull(extVm)) {
//            Device.ExtEntity extEntity = new Device.ExtEntity();
//            extEntity.setStyle(requestExtEntity.getModel());
//            extEntity.setParams(requestExtEntity.getParams());
//            extEntity.setAccess_type(requestExtEntity.getAccessType());
//            extEntity.setAccess_switch_port(requestExtEntity.getAccessSwitchPort());
//            extEntity.setAccess_switch_port_name(requestExtEntity.getAccessSwitchPortName());
//            extEntity.setAccess_ip_address(requestExtEntity.getAccessIpAddress());
//            extEntity.setAccess_mac_address(requestExtEntity.getAccessMacAddress());
//            extEntity.setSnmp_version(requestExtEntity.getSnmpVersion());
//            extEntity.setSnmp_config(requestExtEntity.getSnmpConfig());
//            extEntity.setSnmp_cpu_oid(requestExtEntity.getSnmpCpuOid());
//            extEntity.setSnmp_disk_oid(requestExtEntity.getSnmpDiskOid());
//            extEntity.setSnmp_mem_oid(requestExtEntity.getSnmpMemOid());
//            extEntity.setCcm_port(requestExtEntity.getCcmPort());
//            extEntity.setCcm_name(requestExtEntity.getCcmName());
//            extEntity.setCcm_password(requestExtEntity.getCcmPassword());
//            extEntity.setAccess_protocals(JSONArray.toJSONString(requestExtEntity.getAccessProtocolList()));
//            device.setExt_entity(extEntity);
//        }
//
//        final SystemInfo reqSystemInfo = request.getSystemInfo();
//        if (Objects.nonNull(reqSystemInfo)) {
//            ExtSystemInfo extSystemInfo = new ExtSystemInfo();
//            extSystemInfo.setSystem_type(reqSystemInfo.getSystemType());
//            extSystemInfo.setSystem_architecture(reqSystemInfo.getSystemArchitecture());
//            extSystemInfo.setLanguage(reqSystemInfo.getLanguage());
//            device.setExt_system_info(extSystemInfo);
//        }
        return device;
    }
}
