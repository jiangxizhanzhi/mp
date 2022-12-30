package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.DeviceModel;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterConstant;
import cn.cyberpeace.mp.devopscluster.repository.DeviceMapper;
import cn.cyberpeace.mp.devopscluster.service.fields.DeviceFields;
import cn.hutool.core.collection.CollUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Description
 * @Author shgang
 * @Date 2022/12/20
 */
@Service
public class CpDeviceServiceImpl extends BaseModelServiceImpl<DeviceModel, DeviceMapper, DeviceFields, QueryParams>
        implements CpDeviceService {

    @Autowired
    public CpDeviceServiceImpl(DeviceMapper deviceMapper) {
        setModelMapper(deviceMapper);
    }

    @Override
    public List<Device> findDeviceByIds(List<Integer> ids) {
        HumanCondition cond = new HumanCondition().filterIn(DeviceModel::getId, ids.toArray());
        List<DeviceModel> models = getModelMapper().list(cond);
        return convert(models);
    }

    @Override
    public Device add(Device device) {
        DeviceModel model = new DeviceModel();
        long id = System.currentTimeMillis() / 1000 - DevopsClusterConstant.TIME_STAMP_20221230;
        model.setId(Long.toString(id));
        UUID uuid = UUID.randomUUID();
        model.setUuid(uuid.toString());
        LocalDateTime now = LocalDateTime.now();
        String time = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        model.setUpdatedAt(time);
        model.setDeleted(false);
        model.setCreatedAt(time);
        model.setName(device.getName());
        model.setImage(device.getImage());
        model.setDescription(device.getDescription());
        model.setLogo(device.getLogo());
        model.setImageType(device.getImageType());
        model.setMainType(device.getMaintype());
        model.setRole(device.getRole());
        model.setCategory(device.getCategory());
        model.setSecondCategory(device.getSecondCategory());
        model.setL3Category(device.getL3Category());
        model.setIsImport(device.getIsImport());
        model.setIsInternal(device.getIsInternal());
        model.setIsInit(device.getIsInit());
        if (Objects.nonNull(device.getExtEntity())) {
            model.setExtEntity(JsonCommon.stringify(device.getExtEntity()));
        }
        if (Objects.nonNull(device.getExtEntityAccess())) {
            model.setExtEntityAccess(JsonCommon.stringify(device.getExtEntityAccess()));
        }
        if (Objects.nonNull(device.getExtSystemInfo())) {
            model.setExtSystemInfo(JsonCommon.stringify(device.getExtSystemInfo()));
        }
        if (Objects.nonNull(device.getExtGateway())) {
            model.setExtGateway(JsonCommon.stringify(device.getExtGateway()));
        }
        if (Objects.nonNull(device.getExtVm())) {
            model.setExtVm(JsonCommon.stringify(device.getExtVm()));
        }
        if (Objects.nonNull(device.getExtImageInfo())) {
            model.setExtImageInfo(JsonCommon.stringify(device.getExtImageInfo()));
        }
        if (Objects.nonNull(device.getExtDocker())) {
            model.setExtDocker(JsonCommon.stringify(device.getExtDocker()));
        }
        model.setIsBuildIn(device.getIsBuildIn());
        boolean b = getModelMapper().create(model);
        if (!b) {
            throw new MpRuntimeException("failed to create");
        }
        return convert(Collections.singletonList(model)).get(0);
    }

    @Override
    public Device update(String uuid, Device device) {
        DeviceModel first = getModelMapper().first(new HumanCondition().filterEqual(DeviceModel::getUuid, uuid));
        if (Objects.isNull(first)) {
            throw new MpRuntimeException("DATA NOT EXIST");
        }
        DeviceModel model = new DeviceModel();
        LocalDateTime now = LocalDateTime.now();
        String time = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        model.setUpdatedAt(time);
        model.setName(device.getName());
        model.setImage(device.getImage());
        model.setDescription(device.getDescription());
        model.setLogo(device.getLogo());
        model.setImageType(device.getImageType());
        model.setMainType(device.getMaintype());
        model.setRole(device.getRole());
        model.setCategory(device.getCategory());
        model.setSecondCategory(device.getSecondCategory());
        model.setL3Category(device.getL3Category());
        model.setIsImport(device.getIsImport());
        model.setIsInternal(device.getIsInternal());
        model.setIsInit(device.getIsInit());
        if (Objects.nonNull(device.getExtEntity())) {
            model.setExtEntity(JsonCommon.stringify(device.getExtEntity()));
        }
        if (Objects.nonNull(device.getExtEntityAccess())) {
            model.setExtEntityAccess(JsonCommon.stringify(device.getExtEntityAccess()));
        }
        if (Objects.nonNull(device.getExtSystemInfo())) {
            model.setExtSystemInfo(JsonCommon.stringify(device.getExtSystemInfo()));
        }
        if (Objects.nonNull(device.getExtGateway())) {
            model.setExtGateway(JsonCommon.stringify(device.getExtGateway()));
        }
        model.setExtVm(null);
        model.setExtImageInfo(null);
        model.setExtDocker(null);
        model.setIsBuildIn(device.getIsBuildIn());
        model.setIsHide(device.getIsHide());
        model.setDefaultLaunchImageName(device.getDefaultLaunchImageName());
        model.setId(first.getId());
        ObjServiceResult<DeviceModel> result = this.update(model);
        model = result.sourceObj();
        return convert(Collections.singletonList(model)).get(0);
    }

    @Override
    public Boolean deleteByIds(List<String> uuids) {
        HumanCondition cond = new HumanCondition()
                .filterIn(DeviceModel::getUuid, uuids.toArray());
        int i = getModelMapper().filterDelete(cond);
        return i > 0;
    }

    @Override
    public Device findByUuid(String uuid) {
        HumanCondition cond = new HumanCondition().filterEqual(DeviceModel::getUuid, uuid);
        DeviceModel model = getModelMapper().first(cond);
        return convert(model);
    }

    @Override
    public List<Device> findDevicesByCategories(List<Integer> categories) {
        HumanCondition cond = new HumanCondition();
        if (CollUtil.isNotEmpty(categories)) {
            cond.filterIn(DeviceModel::getCategory, categories.toArray());
        }
        List<DeviceModel> list = getModelMapper().list(cond);
        return convert(list);
    }

    private Device convert(DeviceModel model) {
        Device device = new Device();
        device.setId(Integer.parseInt(model.getId()));
        device.setUuid(model.getUuid());
        device.setUpdatedAt(model.getUpdatedAt());
        device.setDeleted(model.getDeleted());
        device.setCreatedAt(model.getCreatedAt());
        device.setDeletedAt(model.getDeletedAt());
        device.setName(model.getName());
        device.setImage(model.getImage());
        device.setDescription(model.getDescription());
        device.setLogo(model.getLogo());
        device.setImageType(model.getImageType());
        device.setMaintype(model.getMainType());
        device.setRole(model.getRole());
        device.setCategory(model.getCategory());
        device.setSecondCategory(model.getSecondCategory());
        device.setL3Category(model.getL3Category());
        device.setIsImport(model.getIsImport());
        device.setIsInternal(model.getIsInternal());
        device.setIsInit(model.getIsInit());
        if (StringUtils.isNotBlank(model.getExtEntity())) {
            ExtEntity entity = JsonCommon.parse(model.getExtEntity(), ExtEntity.class);
            device.setExtEntity(entity);
        }
        if (StringUtils.isNotBlank(model.getExtSystemInfo())) {
            ExtSystemInfo systemInfo = JsonCommon.parse(model.getExtSystemInfo(), ExtSystemInfo.class);
            device.setExtSystemInfo(systemInfo);
        }
        if (StringUtils.isNotBlank(model.getExtVm())) {
            ExtVm extVm = JsonCommon.parse(model.getExtVm(), ExtVm.class);
            if (Objects.isNull(extVm.getQuickCreateParams())) {
                extVm.setQuickCreateParams(new ArrayList<>());
            }
            device.setExtVm(extVm);
        }
        if (StringUtils.isNotBlank(model.getExtImageInfo())) {
            ExtImageInfo extImageInfo = JsonCommon.parse(model.getExtImageInfo(), ExtImageInfo.class);
            device.setExtImageInfo(extImageInfo);
        }
        device.setDefaultLaunchImageName(model.getDefaultLaunchImageName());
        device.setSuccess(true);
        return device;
    }

    private List<Device> convert(List<DeviceModel> models) {
        List<Device> list = new ArrayList<>(models.size());
        for (DeviceModel model : models) {
            list.add(convert(model));
        }
        return list;
    }
}
