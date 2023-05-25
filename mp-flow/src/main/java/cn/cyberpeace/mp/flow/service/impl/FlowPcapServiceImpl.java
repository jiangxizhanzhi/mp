package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowPcapModel;
import cn.cyberpeace.mp.flow.repository.FlowPcapMapper;
import cn.cyberpeace.mp.flow.service.FlowPcapService;
import cn.cyberpeace.mp.flow.service.fields.FlowPcapFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FlowPcapServiceImpl extends BaseModelServiceImpl<FlowPcapModel, FlowPcapMapper, FlowPcapFields,
        QueryParams> implements FlowPcapService {

    public FlowPcapServiceImpl(FlowPcapMapper stuMapper, FlowPcapFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

}