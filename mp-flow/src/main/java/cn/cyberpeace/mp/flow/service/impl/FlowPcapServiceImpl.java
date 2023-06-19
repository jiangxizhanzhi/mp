package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowPcapModel;
import cn.cyberpeace.mp.flow.repository.FlowPcapMapper;
import cn.cyberpeace.mp.flow.service.FlowPcapService;
import cn.cyberpeace.mp.flow.service.fields.FlowPcapFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FlowPcapServiceImpl extends BaseModelServiceImpl<FlowPcapModel, FlowPcapMapper, FlowPcapFields,
        QueryParams> implements FlowPcapService {

    public FlowPcapServiceImpl(FlowPcapMapper stuMapper, FlowPcapFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    @Override
    public int countAll() {
        return getModelMapper().total(new HumanCondition());
    }

    @Override
    public int deleteAll() {
        return getModelMapper().filterDelete(new HumanCondition());
    }

    @Override
    public FlowPcapModel getByFlowId(String flowId) {
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(FlowPcapModel::getFlowId, flowId);
        return getModelMapper().first(humanCondition);
    }

    @Override
    public List<FlowPcapModel> listAll() {
        return getModelMapper().list(new HumanCondition());
    }
}