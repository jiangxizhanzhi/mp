package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowTaskPortModel;
import cn.cyberpeace.mp.flow.repository.FlowTaskPortMapper;
import cn.cyberpeace.mp.flow.service.FlowTaskPortService;
import cn.cyberpeace.mp.flow.service.fields.FlowTaskPortFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FlowTaskPortServiceImpl extends BaseModelServiceImpl<FlowTaskPortModel, FlowTaskPortMapper, FlowTaskPortFields,
        QueryParams> implements FlowTaskPortService {

    public FlowTaskPortServiceImpl(FlowTaskPortMapper stuMapper, FlowTaskPortFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

}