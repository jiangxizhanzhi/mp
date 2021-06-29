package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowModel;
import cn.cyberpeace.mp.flow.repository.FlowMapper;
import cn.cyberpeace.mp.flow.service.FlowService;
import cn.cyberpeace.mp.flow.service.field.FlowFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description: 流量service
 * @Autho qiaokang
 * @Date：2021/6/10 10:31
 */
@Service
@Slf4j
public class FlowServiceImpl extends BaseModelServiceImpl<FlowModel, FlowMapper, FlowFields, QueryParams>
        implements FlowService {

    public FlowServiceImpl(FlowMapper stuMapper, FlowFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }
}
