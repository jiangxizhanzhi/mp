package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowProtocolModel;
import cn.cyberpeace.mp.flow.repository.FlowProtocolMapper;
import cn.cyberpeace.mp.flow.service.FlowProtocolService;
import cn.cyberpeace.mp.flow.service.fields.FlowProtocolFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FlowProtocolServiceImpl extends BaseModelServiceImpl<FlowProtocolModel, FlowProtocolMapper, FlowProtocolFields,
        QueryParams> implements FlowProtocolService {

    public FlowProtocolServiceImpl(FlowProtocolMapper stuMapper, FlowProtocolFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    
    @Override
    public List<FlowProtocolModel> selectByFlowId(String flowId) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual("flowId", flowId);
        return getModelMapper().list(condition);
    }

    
    @Override
    public void deleteByFlowId(String flowId) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual("flowId", flowId);
        getModelMapper().filterDelete(condition);
    }

    @Override
    public int deleteAll() {
        return getModelMapper().filterDelete(new HumanCondition());
    }
}