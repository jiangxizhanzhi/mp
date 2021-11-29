package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import cn.cyberpeace.mp.flow.repository.FlowScriptParamMapper;
import cn.cyberpeace.mp.flow.service.FlowScriptParamService;
import cn.cyberpeace.mp.flow.service.field.FlowScriptParamFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FlowScriptParamServiceImpl extends BaseModelServiceImpl<FlowScriptParamModel, FlowScriptParamMapper, FlowScriptParamFields,
        QueryParams> implements FlowScriptParamService {

    public FlowScriptParamServiceImpl(FlowScriptParamMapper stuMapper, FlowScriptParamFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    @Override
    public List<FlowScriptParamModel> selectByFlowId(String flowId) {
        return getModelMapper().selectByFlowId(flowId);
    }

    @Override
    public List<FlowScriptParamModel> selectByFlowIds(List<String> flowIds) {
        return getModelMapper().selectByFlowIds(flowIds);
    }

    @Override
    public int deleteByFlowId(String flowId) {
        return getModelMapper().deleteByFlowId(flowId);
    }

    @Override
    public int deleteByFlowIds(List<String> flowIds) {
        return getModelMapper().deleteByFlowIds(flowIds);
    }

}