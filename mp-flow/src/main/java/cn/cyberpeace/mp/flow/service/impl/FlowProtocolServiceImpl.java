package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowProtocolModel;
import cn.cyberpeace.mp.flow.repository.FlowProtocolMapper;
import cn.cyberpeace.mp.flow.service.FlowProtocolService;
import cn.cyberpeace.mp.flow.service.fields.FlowProtocolFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FlowProtocolServiceImpl extends BaseModelServiceImpl<FlowProtocolModel, FlowProtocolMapper, FlowProtocolFields,
        QueryParams> implements FlowProtocolService {

    public FlowProtocolServiceImpl(FlowProtocolMapper stuMapper, FlowProtocolFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

}