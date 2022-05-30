package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.enums.PublicEnum;
import cn.cyberpeace.mp.devops.cluster.enums.StatusEnum;
import cn.cyberpeace.mp.devops.cluster.po.TargetPO;
import cn.cyberpeace.mp.devops.cluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devops.cluster.service.DevopsTargetService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
@RequiredArgsConstructor
public class DevopsTargetServiceImpl implements DevopsTargetService {

    private static final String VM = "vm";

    private static final String CONTAINER = "container";

    private final DeviceTargetMapper deviceTargetMapper;

    @Override
    public Integer totalCount(ImagePageDTO dto) {
        return deviceTargetMapper.totalCount(dto);
    }

    @Transactional
    @Override
    public Integer updateById(Device device) {
        if (Objects.nonNull(device) && device.isSuccess()) {
            TargetPO oldTargetPO = deviceTargetMapper.findByUuid(device.getUuid());
            if (Objects.nonNull(oldTargetPO)) {
                TargetPO targetPO = convertDevice(device);
                targetPO.setIsPublic(oldTargetPO.getIsPublic());
                return deviceTargetMapper.updateById(targetPO);
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
        List<TargetPO> targetPOS = deviceList.stream().map(this::convertToTargetModel)
                .collect(Collectors.toList());
        return deviceTargetMapper.batchInsert(targetPOS);
    }

    @Override
    public Map<String,Object> countTarget() {
        return deviceTargetMapper.countTarget();
    }

    @Override
    public Map<String,Object> countVmTarget() {
        return deviceTargetMapper.countVmTarget();
    }

    @Override
    public Map<String,Object> countEntityTarget() {
        return deviceTargetMapper.countEntityTarget();
    }

    @Transactional
    @Override
    public int deleteAllTarget() {
        return deviceTargetMapper.deleteAllTarget();
    }

    protected TargetPO convertToTargetModel(Device device){
        TargetPO targetPO = convertDevice(device);
        targetPO.setIsPublic(PublicEnum.ALL_PUBLIC);
        targetPO.setUseStatus(StatusEnum.NO);
        targetPO.setReferenceStatus(NumberUtils.INTEGER_ZERO);
        return targetPO;
    }

    /**
     * 转换Device
     * @param device
     * @return
     */
    protected TargetPO convertDevice(Device device){
        TargetPO targetPO = new TargetPO();
        targetPO.setId(device.getId().toString());
        targetPO.setUuid(device.getUuid());
        targetPO.setName(device.getName());
        targetPO.setLogo(device.getLogo());
        targetPO.setRole(device.getRole());
        targetPO.setMainCategory(device.getCategory());
        targetPO.setSubCategory(device.getSecondCategory());
        targetPO.setThirdCategory(device.getL3Category());
        targetPO.setImageType(device.getImageType());
        targetPO.setIsHide(Objects.nonNull(device.getIsHide()) ? device.getIsHide() : Boolean.FALSE);
        String imageType = device.getImageType();
        targetPO.setIsImport(device.getIsImport());
        ExtVm extVm = device.getExtVm();
        ExtDocker extDocker = device.getExtDocker();
        if (StringUtils.isNotBlank(imageType) && imageType.equals(VM) && Objects.nonNull(extVm)){
            targetPO.setQuickCreateSupport(extVm.getQuickCreateSupport());
            targetPO.setInitSupport(extVm.getInitSupport());
        }
        else if (StringUtils.isNotBlank(imageType) && imageType.equals(CONTAINER) && Objects.nonNull(extDocker)){
            targetPO.setQuickCreateSupport(extDocker.getQuickCreateSupport());
            targetPO.setInitSupport(extDocker.getInitSupport());
        }else{
            targetPO.setQuickCreateSupport(Boolean.FALSE);
            targetPO.setInitSupport(Boolean.FALSE);
        }

        targetPO.setCreateTime(TimeUtils.utcStringToLocalDateTime(device.getCreatedAt()));
        targetPO.setUpdateTime(TimeUtils.utcStringToLocalDateTime(device.getUpdatedAt()));
        ExtEntity extEntity = device.getExtEntity();
        if (Objects.nonNull(extEntity)){
            targetPO.setInPosition(extEntity.getEntityStatus() ? StatusEnum.YES : StatusEnum.NO);
        }

        ExtImageInfo extImageInfo = device.getExtImageInfo();
        if (Objects.nonNull(extImageInfo)){
            targetPO.setImageStatus(extImageInfo.getImageStatus());
            targetPO.setImageSource(extImageInfo.getImageSource());
            targetPO.setBaseImageSupport(extImageInfo.getBaseImageSupport());
        }

        ExtSystemInfo extSystemInfo = device.getExtSystemInfo();
        if (Objects.nonNull(extSystemInfo)){
            targetPO.setParentSystemType(extSystemInfo.getParentSystemType());
            targetPO.setSystemType(extSystemInfo.getSystemType());
            targetPO.setLanguage(extSystemInfo.getLanguage());
            targetPO.setSystemArchitecure(extSystemInfo.getSystemArchitecture());
        }

        return targetPO;
    }
}
