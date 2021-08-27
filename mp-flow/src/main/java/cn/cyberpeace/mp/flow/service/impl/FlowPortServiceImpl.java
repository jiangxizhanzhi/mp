package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowPortModel;
import cn.cyberpeace.mp.flow.repository.FlowPortMapper;
import cn.cyberpeace.mp.flow.service.FlowPortService;
import cn.cyberpeace.mp.flow.service.field.FlowPortFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description: 流量网口service
 * @Autho qiaokang
 * @Date：2021/6/10 10:31
 */
@Service
@Slf4j
public class FlowPortServiceImpl extends BaseModelServiceImpl<FlowPortModel, FlowPortMapper, FlowPortFields, QueryParams> implements FlowPortService {

    public FlowPortServiceImpl(FlowPortMapper stuMapper, FlowPortFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

}
