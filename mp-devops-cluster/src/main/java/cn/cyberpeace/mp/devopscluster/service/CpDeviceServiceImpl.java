package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.DeviceModel;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devopscluster.repository.DeviceMapper;
import cn.cyberpeace.mp.devopscluster.service.fields.DeviceFields;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    private List<Device> convert(List<DeviceModel> models) {
        List<Device> list = new ArrayList<>(models.size());
        for (DeviceModel model : models) {
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
            device.setMaintype(model.getMaintype());
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
                device.setExtVm(extVm);
            }
            if (StringUtils.isNotBlank(model.getExtImageInfo())) {
                ExtImageInfo extImageInfo = JsonCommon.parse(model.getExtImageInfo(), ExtImageInfo.class);
                device.setExtImageInfo(extImageInfo);
            }
            device.setDefaultLaunchImageName(model.getDefaultLaunchImageName());
            device.setSuccess(true);
            list.add(device);
        }
        return list;
    }
}
