package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskCountModel;
import cn.cyberpeace.mp.flow.v2.repository.FlowTaskCountMapper;
import cn.cyberpeace.mp.flow.v2.service.fields.FlowTaskCountFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlowTaskCountService extends BaseModelServiceImpl<FlowTaskCountModel, FlowTaskCountMapper, FlowTaskCountFields, QueryParams> {


    @Autowired
    public FlowTaskCountService(FlowTaskCountMapper modelMapper, FlowTaskCountFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

}
