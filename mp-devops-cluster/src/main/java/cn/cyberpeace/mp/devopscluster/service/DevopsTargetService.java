package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtDocker;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtEntity;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtImageInfo;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtSystemInfo;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtVm;
import cn.cyberpeace.mp.devopscluster.converter.SupportExportConvert;
import cn.cyberpeace.mp.devopscluster.converter.model.ImageConvertModel;
import cn.cyberpeace.mp.devopscluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.enums.ImageTypeEnum;
import cn.cyberpeace.mp.devopscluster.enums.PublicEnum;
import cn.cyberpeace.mp.devopscluster.enums.StatusEnum;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.utils.POLogoUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DevopsTargetService {

    private final DeviceTargetMapper deviceTargetMapper;

    private final SupportExportConvert supportExportConvert;

    
    public List<DeviceTargetPO> page(ImagePageDTO dto) {
        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.page(dto);
        POLogoUtils.changeLogoPathToUrl(deviceTargetPOS);
        return deviceTargetPOS;
    }

    
    public Integer totalCount(ImagePageDTO dto) {
        return deviceTargetMapper.totalCount(dto);
    }

    
    @Transactional
    public Integer updateById(Device device) {
        DeviceTargetPO oldDeviceTargetPO = deviceTargetMapper.findByUuid(device.getUuid());
        if (Objects.nonNull(oldDeviceTargetPO)) {
            DeviceTargetPO deviceTargetPO = convertDevice(device);
            deviceTargetPO.setIsPublic(oldDeviceTargetPO.getIsPublic());
            return deviceTargetMapper.updateById(deviceTargetPO);
        }
        return 0;
    }

    
    @Transactional
    public Integer insert(List<Device> deviceList) {
        if (CollectionUtils.isEmpty(deviceList)) {
            return 0;
        }
        for (var d : deviceList) {
            d.setSupportExport(this.getSupportExport(d));
        }
        List<DeviceTargetPO> deviceTargetPOS = deviceList.stream().map(this::convertToTargetModel)
                                                       .collect(Collectors.toList());
        var user = UserCommon.getCurrentUser();
        deviceTargetPOS.forEach(d -> {
            d.setCreateUser(user != null ? user.getId() : StringUtils.EMPTY);
            d.setCreateUserName(user != null ? user.getName() : StringUtils.EMPTY);
        });
        return deviceTargetMapper.batchInsert(deviceTargetPOS);
    }

    public void updateInPosition(List<Device> deviceList) {
        if (deviceList == null) {
            return;
        }
        List<DeviceTargetPO> update = new ArrayList<>();
        for (var l : deviceList) {
            if (l.getExtEntity() != null) {
                DeviceTargetPO deviceTargetPO = new DeviceTargetPO();
                deviceTargetPO.setUuid(l.getUuid());
                deviceTargetPO.setInPosition(Boolean.TRUE.equals(l.getExtEntity().getEntityStatus()) ? StatusEnum.YES : StatusEnum.NO);
                update.add(deviceTargetPO);
            }
        }
        if (CollectionUtils.isNotEmpty(update)) {
            deviceTargetMapper.batchUpdateInPosition(update);
        }
    }

    
    public Map<String, Object> countTarget() {
        return deviceTargetMapper.countTarget();
    }

    
    public Map<String, Object> countVmTarget() {
        return deviceTargetMapper.countVmTarget();
    }

    
    public Map<String, Object> countEntityTarget() {
        return deviceTargetMapper.countEntityTarget();
    }

    
    @Transactional
    public int deleteAllTarget() {
        return deviceTargetMapper.deleteAllTarget();
    }

    
    @Transactional
    public Integer delete(ImageDeleteDTO dto) {
        return deviceTargetMapper.delete(dto);
    }

    protected DeviceTargetPO convertToTargetModel(Device device) {
        DeviceTargetPO deviceTargetPO = convertDevice(device);
        deviceTargetPO.setIsPublic(PublicEnum.ALL_PUBLIC);
        deviceTargetPO.setUseStatus(StatusEnum.NO);
        deviceTargetPO.setReferenceStatus(NumberUtils.INTEGER_ZERO);
        return deviceTargetPO;
    }

    
    protected DeviceTargetPO convertDevice(Device device) {
        DeviceTargetPO deviceTargetPO = new DeviceTargetPO();
        deviceTargetPO.setId(device.getId().toString());
        deviceTargetPO.setUuid(device.getUuid());
        deviceTargetPO.setName(device.getName());
        deviceTargetPO.setLogo(DefaultImgUtil.fdfsUrlToPath(device.getLogo()));
        deviceTargetPO.setRole(device.getRole());
        deviceTargetPO.setMainCategory(device.getCategory());
        deviceTargetPO.setSubCategory(device.getSecondCategory());
        deviceTargetPO.setThirdCategory(device.getL3Category());
        deviceTargetPO.setImageType(device.getImageType());
        deviceTargetPO.setIsHide(Objects.nonNull(device.getIsHide()) ? device.getIsHide() : Boolean.FALSE);
        deviceTargetPO.setIsInit(Objects.nonNull(device.getIsInit()) ? device.getIsInit() : Boolean.FALSE);
        if (device.getSupportExport() != null) {
            deviceTargetPO.setSupportExport(device.getSupportExport());
        }
        String imageType = device.getImageType();
        deviceTargetPO.setIsImport(device.getIsImport());
        ExtVm extVm = device.getExtVm();
        ExtDocker extDocker = device.getExtDocker();
        if (StringUtils.isNotBlank(imageType) && (imageType.equals(ImageTypeEnum.VM.toValue()) || imageType.equals(ImageTypeEnum.DYNAMIPS.toValue()))
                && Objects.nonNull(extVm)) {
            deviceTargetPO.setQuickCreateSupport(extVm.getQuickCreateSupport());
            deviceTargetPO.setInitSupport(extVm.getInitSupport());
            deviceTargetPO.setInstanceState(extVm.getInstanceState());
        } else if (StringUtils.isNotBlank(imageType) && imageType.equals(ImageTypeEnum.CONTAINER.toValue()) && Objects.nonNull(extDocker)) {
            deviceTargetPO.setQuickCreateSupport(extDocker.getQuickCreateSupport());

            
            deviceTargetPO.setInitSupport(true);
            deviceTargetPO.setInstanceState(extDocker.getInstanceState());
        } else {
            deviceTargetPO.setQuickCreateSupport(Boolean.FALSE);
            deviceTargetPO.setInitSupport(Boolean.FALSE);
        }

        deviceTargetPO.setCreateTime(TimeUtils.utcStringToLocalDateTime(device.getCreatedAt()));
        deviceTargetPO.setUpdateTime(TimeUtils.utcStringToLocalDateTime(device.getUpdatedAt()));
        ExtEntity extEntity = device.getExtEntity();
        if (Objects.nonNull(extEntity)) {
            deviceTargetPO.setInPosition(extEntity.getEntityStatus() ? StatusEnum.YES : StatusEnum.NO);
        }

        ExtImageInfo extImageInfo = device.getExtImageInfo();
        if (Objects.nonNull(extImageInfo)) {
            deviceTargetPO.setImageStatus(extImageInfo.getImageStatus());
            deviceTargetPO.setImageSource(extImageInfo.getImageSource());
            deviceTargetPO.setBaseImageSupport(extImageInfo.getBaseImageSupport());
        }

        ExtSystemInfo extSystemInfo = device.getExtSystemInfo();
        if (Objects.nonNull(extSystemInfo)) {
            deviceTargetPO.setParentSystemType(extSystemInfo.getParentSystemType());
            deviceTargetPO.setSystemType(extSystemInfo.getSystemType());
            deviceTargetPO.setLanguage(extSystemInfo.getLanguage());
            deviceTargetPO.setSystemArchitecure(extSystemInfo.getSystemArchitecture());
        }

        return deviceTargetPO;
    }

    private Boolean getSupportExport(Device device) {
        
        if (Boolean.TRUE.equals(device.getIsImport() || Boolean.TRUE.equals(device.getIsInit()) || Boolean.TRUE.equals(device.getIsInternal()))) {
            return false;
        } else if (device.getExtImageInfo() != null && StringUtils.isNotEmpty(device.getExtImageInfo().getBaseImageName())) {
            
            ImagePageDTO pageDTO = new ImagePageDTO();
            pageDTO.setName(device.getExtImageInfo().getBaseImageName());
            pageDTO.setLikeName(false);
            var result = this.supportExportConvert.page(pageDTO);
            var list = result.getList();
            if (CollectionUtils.isNotEmpty(list)) {
                ImageConvertModel baseImage = list.get(0);
                if (Boolean.TRUE.equals(baseImage.getIsImport() || Boolean.TRUE.equals(baseImage.getIsInit()) || Boolean.TRUE.equals(baseImage.getIsInternal()))) {
                    return false;
                }
                if (Boolean.FALSE.equals(baseImage.getSupportExport())) {
                    return false;
                }
            }
        }
        return true;
    }

}
