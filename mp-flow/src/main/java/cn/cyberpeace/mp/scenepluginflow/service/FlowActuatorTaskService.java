package cn.cyberpeace.mp.scenepluginflow.service;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowTaskTaskMapper;
import cn.cyberpeace.mp.flow.request.queryparam.FlowTaskPageQueryParams;
import cn.cyberpeace.mp.flow.response.FlowTaskPageResponse;
import cn.cyberpeace.mp.flow.service.fields.FlowTaskTaskFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
public class FlowActuatorTaskService extends BaseModelServiceImpl<FlowTaskTaskModel, FlowTaskTaskMapper, FlowTaskTaskFields, FlowTaskPageQueryParams> {

    private FlowTaskTaskMapper flowTaskTaskMapper;

    @Autowired
    public FlowActuatorTaskService(FlowTaskTaskMapper flowTaskTaskMapper) {
        super();
        super.setModelMapper(flowTaskTaskMapper);
        this.flowTaskTaskMapper = flowTaskTaskMapper;
    }

    public ListObjServiceResult<FlowTaskPageResponse> taskList(FlowTaskPageQueryParams params) {
        int total = flowTaskTaskMapper.countTasks(params);
        if (total == 0) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        } else {
            int offset = (params.getPage() - 1) * params.getPageSize();
            int limit = params.getPageSize();
            var list = flowTaskTaskMapper.listTasks(params, offset, limit);
            return new ListObjServiceResult<>(new ListObjResult<>(list, total));
        }
    }
}
