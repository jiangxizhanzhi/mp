package cn.cyberpeace.mp.devopscluster.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devopscluster.model.DeviceToolModel;
import cn.cyberpeace.mp.devopscluster.repository.DeviceToolMapper;
import cn.cyberpeace.mp.devopscluster.service.DeviceToolService;
import cn.cyberpeace.mp.devopscluster.service.fields.AlarmStatisticsFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DeviceToolServiceImpl extends BaseModelServiceImpl<DeviceToolModel, DeviceToolMapper, AlarmStatisticsFields,
        QueryParams> implements DeviceToolService {

    public DeviceToolServiceImpl(DeviceToolMapper stuMapper, AlarmStatisticsFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }


    @Override
    public void updateByDeviceUuid(List<DeviceToolModel> tools, String uuid) {
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(DeviceToolModel::getDeviceUuid, uuid);
        getModelMapper().filterDelete(humanCondition);
        if (tools != null && !tools.isEmpty()) {
            for (DeviceToolModel tool : tools) {
                tool.setDeviceUuid(uuid);
            }
            getModelMapper().bulkCreate(tools);
        }
    }
}
