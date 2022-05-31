package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.enums.PublicEnum;
import cn.cyberpeace.mp.devops.cluster.enums.StatusEnum;
import cn.cyberpeace.mp.devops.cluster.po.DeviceTargetPO;
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
            DeviceTargetPO oldDeviceTargetPO = deviceTargetMapper.findByUuid(device.getUuid());
            if (Objects.nonNull(oldDeviceTargetPO)) {
                DeviceTargetPO deviceTargetPO = convertDevice(device);
                deviceTargetPO.setIsPublic(oldDeviceTargetPO.getIsPublic());
                return deviceTargetMapper.updateById(deviceTargetPO);
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
        List<DeviceTargetPO> deviceTargetPOS = deviceList.stream().map(this::convertToTargetModel)
                .collect(Collectors.toList());
        return deviceTargetMapper.batchInsert(deviceTargetPOS);
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

    protected DeviceTargetPO convertToTargetModel(Device device){
        DeviceTargetPO deviceTargetPO = convertDevice(device);
        deviceTargetPO.setIsPublic(PublicEnum.ALL_PUBLIC);
        deviceTargetPO.setUseStatus(StatusEnum.NO);
        deviceTargetPO.setReferenceStatus(NumberUtils.INTEGER_ZERO);
        return deviceTargetPO;
    }

    /**
     * 转换Device
     * @param device
     * @return
     */
    protected DeviceTargetPO convertDevice(Device device){
        DeviceTargetPO deviceTargetPO = new DeviceTargetPO();
        deviceTargetPO.setId(device.getId().toString());
        deviceTargetPO.setUuid(device.getUuid());
        deviceTargetPO.setName(device.getName());
        deviceTargetPO.setLogo(device.getLogo());
        deviceTargetPO.setRole(device.getRole());
        deviceTargetPO.setMainCategory(device.getCategory());
        deviceTargetPO.setSubCategory(device.getSecondCategory());
        deviceTargetPO.setThirdCategory(device.getL3Category());
        deviceTargetPO.setImageType(device.getImageType());
        deviceTargetPO.setIsHide(Objects.nonNull(device.getIsHide()) ? device.getIsHide() : Boolean.FALSE);
        String imageType = device.getImageType();
        deviceTargetPO.setIsImport(device.getIsImport());
        ExtVm extVm = device.getExtVm();
        ExtDocker extDocker = device.getExtDocker();
        if (StringUtils.isNotBlank(imageType) && imageType.equals(VM) && Objects.nonNull(extVm)){
            deviceTargetPO.setQuickCreateSupport(extVm.getQuickCreateSupport());
            deviceTargetPO.setInitSupport(extVm.getInitSupport());
        }
        else if (StringUtils.isNotBlank(imageType) && imageType.equals(CONTAINER) && Objects.nonNull(extDocker)){
            deviceTargetPO.setQuickCreateSupport(extDocker.getQuickCreateSupport());
            deviceTargetPO.setInitSupport(extDocker.getInitSupport());
        }else{
            deviceTargetPO.setQuickCreateSupport(Boolean.FALSE);
            deviceTargetPO.setInitSupport(Boolean.FALSE);
        }

        deviceTargetPO.setCreateTime(TimeUtils.utcStringToLocalDateTime(device.getCreatedAt()));
        deviceTargetPO.setUpdateTime(TimeUtils.utcStringToLocalDateTime(device.getUpdatedAt()));
        ExtEntity extEntity = device.getExtEntity();
        if (Objects.nonNull(extEntity)){
            deviceTargetPO.setInPosition(extEntity.getEntityStatus() ? StatusEnum.YES : StatusEnum.NO);
        }

        ExtImageInfo extImageInfo = device.getExtImageInfo();
        if (Objects.nonNull(extImageInfo)){
            deviceTargetPO.setImageStatus(extImageInfo.getImageStatus());
            deviceTargetPO.setImageSource(extImageInfo.getImageSource());
            deviceTargetPO.setBaseImageSupport(extImageInfo.getBaseImageSupport());
        }

        ExtSystemInfo extSystemInfo = device.getExtSystemInfo();
        if (Objects.nonNull(extSystemInfo)){
            deviceTargetPO.setParentSystemType(extSystemInfo.getParentSystemType());
            deviceTargetPO.setSystemType(extSystemInfo.getSystemType());
            deviceTargetPO.setLanguage(extSystemInfo.getLanguage());
            deviceTargetPO.setSystemArchitecure(extSystemInfo.getSystemArchitecture());
        }

        return deviceTargetPO;
    }
}
