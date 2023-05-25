package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowTaskTaskMapper;
import cn.cyberpeace.mp.flow.service.FlowTaskTaskService;
import cn.cyberpeace.mp.flow.service.fields.FlowTaskTaskFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FlowTaskTaskServiceImpl extends BaseModelServiceImpl<FlowTaskTaskModel, FlowTaskTaskMapper, FlowTaskTaskFields,
        QueryParams> implements FlowTaskTaskService {

    public FlowTaskTaskServiceImpl(FlowTaskTaskMapper stuMapper, FlowTaskTaskFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

}