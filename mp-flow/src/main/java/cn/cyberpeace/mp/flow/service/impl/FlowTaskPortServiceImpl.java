package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowTaskPortModel;
import cn.cyberpeace.mp.flow.repository.FlowTaskPortMapper;
import cn.cyberpeace.mp.flow.service.FlowTaskPortService;
import cn.cyberpeace.mp.flow.service.fields.FlowTaskPortFields;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FlowTaskPortServiceImpl extends BaseModelServiceImpl<FlowTaskPortModel, FlowTaskPortMapper, FlowTaskPortFields,
        QueryParams> implements FlowTaskPortService {

    public FlowTaskPortServiceImpl(FlowTaskPortMapper stuMapper, FlowTaskPortFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    
    @Override
    public FlowTaskPortModel findByNetworkIdAndActuatorId(String networkId, String actuatorId) {
        log.info("FlowTaskPortServiceImpl.findByNetworkIdAndActuatorId begin. networkId:{}, actuatorId:{}", networkId, actuatorId);
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskPortModel::getNetworkId, networkId);
        condition.filterEqual(FlowTaskPortModel::getActuatorId, actuatorId);
        List<FlowTaskPortModel> flowTaskPortModelList = getModelMapper().list(condition);
        if (CollectionUtils.isNotEmpty(flowTaskPortModelList)) {
            FlowTaskPortModel result = flowTaskPortModelList.get(0);
            log.info("FlowTaskPortServiceImpl.findByNetworkIdAndActuatorId end. result:{}", result);
            return result;
        }
        log.info("FlowTaskPortServiceImpl.findByNetworkIdAndActuatorId end.");
        return null;
    }

}