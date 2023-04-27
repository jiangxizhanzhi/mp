package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;
import cn.cyberpeace.mp.flow.repository.FlowTaskConfMapper;
import cn.cyberpeace.mp.flow.service.FlowTaskConfService;
import cn.cyberpeace.mp.flow.service.fields.FlowTaskConfFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FlowTaskConfServiceImpl extends BaseModelServiceImpl<FlowTaskConfModel, FlowTaskConfMapper, FlowTaskConfFields,
        QueryParams> implements FlowTaskConfService {

    public FlowTaskConfServiceImpl(FlowTaskConfMapper stuMapper, FlowTaskConfFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

}