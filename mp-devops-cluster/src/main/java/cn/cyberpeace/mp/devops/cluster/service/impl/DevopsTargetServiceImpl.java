package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.enums.PublicEnum;
import cn.cyberpeace.mp.devops.cluster.enums.StatusEnum;
import cn.cyberpeace.mp.devops.cluster.model.TargetModel;
import cn.cyberpeace.mp.devops.cluster.repository.ImageMapper;
import cn.cyberpeace.mp.devops.cluster.service.DevopsTargetService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wanggang
 */
@Service
public class DevopsTargetServiceImpl implements DevopsTargetService {

    private static final String VM = "vm";

    private static final String CONTAINER = "container";

    private ImageMapper imageMapper;

    @Autowired
    public DevopsTargetServiceImpl(ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    @Override
    public Integer totalCount(ImagePageDTO dto) {
        return imageMapper.totalCount(dto);
    }

    @Transactional
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

    @Transactional
    @Override
    public Integer insert(List<Device> deviceList) {
        if (CollectionUtils.isEmpty(deviceList)){
            return 0;
        }
        List<TargetModel> targetModels = deviceList.stream().map(device -> convertToTargetModel(device))
                .collect(Collectors.toList());
        return imageMapper.batchInsert(targetModels);
    }

    @Override
    public Map<String,Object> countTarget() {
        return imageMapper.countTarget();
    }

    @Override
    public Map<String,Object> countVmTarget() {
        return imageMapper.countVmTarget();
    }

    @Override
    public Map<String,Object> countEntityTarget() {
        return imageMapper.countEntityTarget();
    }

    protected TargetModel convertToTargetModel(Device device){
        TargetModel targetModel = convertDevice(device);
        targetModel.setIsPublic(PublicEnum.ALL_PUBLIC);
        targetModel.setUseStatus(StatusEnum.NO);
        targetModel.setReferenceStatus(NumberUtils.INTEGER_ZERO);
        return targetModel;
    }

    /**
     * 转换Device
     * @param device
     * @return
     */
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
        targetModel.setIsImport(device.getIsImport());
        ExtVm extVm = device.getExtVm();
        ExtDocker extDocker = device.getExtDocker();
        if (StringUtils.isNotBlank(imageType) && imageType.equals(VM) && Objects.nonNull(extVm)){
            targetModel.setQuickCreateSupport(extVm.getQuickCreateSupport());
            targetModel.setInitSupport(extVm.getInitSupport());
        }
        else if (StringUtils.isNotBlank(imageType) && imageType.equals(CONTAINER) && Objects.nonNull(extDocker)){
            targetModel.setQuickCreateSupport(extDocker.getQuickCreateSupport());
            targetModel.setInitSupport(extDocker.getInitSupport());
        }else{
            targetModel.setQuickCreateSupport(Boolean.FALSE);
            targetModel.setInitSupport(Boolean.FALSE);
        }

        targetModel.setCreateTime(TimeUtils.utcStringToLocalDateTime(device.getCreatedAt()));
        targetModel.setUpdateTime(TimeUtils.utcStringToLocalDateTime(device.getUpdatedAt()));
        ExtEntity extEntity = device.getExtEntity();
        if (Objects.nonNull(extEntity)){
            targetModel.setInPosition(extEntity.getEntityStatus() ? StatusEnum.YES : StatusEnum.NO);
        }

        ExtImageInfo extImageInfo = device.getExtImageInfo();
        if (Objects.nonNull(extImageInfo)){
            targetModel.setImageStatus(extImageInfo.getImageStatus());
            targetModel.setImageSource(extImageInfo.getImageSource());
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
