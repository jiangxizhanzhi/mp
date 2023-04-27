package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowTaskRecordModel;
import cn.cyberpeace.mp.flow.repository.FlowTaskRecordMapper;
import cn.cyberpeace.mp.flow.service.FlowTaskRecordService;
import cn.cyberpeace.mp.flow.service.fields.FlowTaskRecordFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FlowTaskRecordServiceImpl extends BaseModelServiceImpl<FlowTaskRecordModel, FlowTaskRecordMapper, FlowTaskRecordFields,
        QueryParams> implements FlowTaskRecordService {

    public FlowTaskRecordServiceImpl(FlowTaskRecordMapper stuMapper, FlowTaskRecordFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

}