package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.util.LocalDateTimeUtils;
import cn.cyberpeace.mp.devops.cluster.enums.PublicEnum;
import cn.cyberpeace.mp.devops.cluster.enums.StatusEnum;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.TargetModel;
import cn.cyberpeace.mp.devops.cluster.model.common.*;
import cn.cyberpeace.mp.devops.cluster.repository.ImageMapper;
import cn.cyberpeace.mp.devops.cluster.service.DevopsTargetService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class DevopsDevopsTargetServiceImpl implements DevopsTargetService {

    private ImageMapper imageMapper;

    @Autowired
    public DevopsDevopsTargetServiceImpl(ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    @Transactional
    @Override
    public Integer insert(Device device) {
        if (Objects.nonNull(device) && device.isSuccess()) {
            TargetModel targetModel = convertDevice(device);
            targetModel.setIsPublic(PublicEnum.ALL_PUBLIC);
            targetModel.setUseStatus(StatusEnum.NO);
            targetModel.setReferenceStatus(NumberUtils.INTEGER_ZERO);
            return imageMapper.insert(targetModel);
        }
        return 0;
    }

    @Override
    public Integer updateById(Device device) {
        if (Objects.nonNull(device) && device.isSuccess()) {
            TargetModel oldTargetModel = imageMapper.findByUuid(device.getUuid());
            if (Objects.nonNull(oldTargetModel)) {
                TargetModel targetModel = convertDevice(device);
                targetModel.setIsPublic(oldTargetModel.getIsPublic());
                return imageMapper.updateById(targetModel);
            }
        }
        return 0;
    }

    protected TargetModel convertDevice(Device device){
        TargetModel targetModel = new TargetModel();
        targetModel.setId(device.getId().toString());
        targetModel.setUuid(device.getUuid());
        targetModel.setName(device.getName());
        targetModel.setRole(device.getRole());
        targetModel.setMainCategory(device.getCategory());
        targetModel.setSubCategory(device.getSecondCategory());
        targetModel.setThirdCategory(device.getL3Category());
        targetModel.setImageType(device.getImageType());
        String imageType = device.getImageType();
        ExtVm extVm = device.getExtVm();
        ExtDocker extDocker = device.getExtDocker();
        if (StringUtils.isNotBlank(imageType) && imageType.equals("vm") && Objects.nonNull(extVm)){
            targetModel.setQuickCreateSupport(extVm.getQuickCreateSupport());
        }
        else if (StringUtils.isNotBlank(imageType) && imageType.equals("container") && Objects.nonNull(extDocker)){
            targetModel.setQuickCreateSupport(extDocker.getQuickCreateSupport());
        }else{
            targetModel.setQuickCreateSupport(Boolean.FALSE);
        }

        targetModel.setCreateTime(LocalDateTimeUtils.convertToLocalDateTime(device.getCreatedAt()));
        targetModel.setUpdateTime(LocalDateTimeUtils.convertToLocalDateTime(device.getUpdatedAt()));
        ExtEntity extEntity = device.getExtEntity();
        if (Objects.nonNull(extEntity)){
            targetModel.setInPosition(extEntity.getEntityStatus() ? StatusEnum.YES : StatusEnum.NO);
        }

        ExtImageInfo extImageInfo = device.getExtImageInfo();
        if (Objects.nonNull(extImageInfo)){
            targetModel.setImageStatus(extImageInfo.getImageStatus());
            targetModel.setBaseImageSupport(extImageInfo.getBaseImageSupport());
        }

        ExtSystemInfo extSystemInfo = device.getExtSystemInfo();
        if (Objects.nonNull(extSystemInfo)){
            targetModel.setSystemType(extSystemInfo.getSystemType());
            targetModel.setLanguage(extSystemInfo.getLanguage());
            targetModel.setSystemArchitecure(extSystemInfo.getSystemArchitecture());
        }

        return targetModel;
    }
}
