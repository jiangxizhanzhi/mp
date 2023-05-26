package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowTaskTaskMapper;
import cn.cyberpeace.mp.flow.service.FlowTaskTaskService;
import cn.cyberpeace.mp.flow.service.fields.FlowTaskTaskFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FlowTaskTaskServiceImpl extends BaseModelServiceImpl<FlowTaskTaskModel, FlowTaskTaskMapper, FlowTaskTaskFields,
        QueryParams> implements FlowTaskTaskService {

    public FlowTaskTaskServiceImpl(FlowTaskTaskMapper stuMapper, FlowTaskTaskFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    @Override
    public int updateTaskStatus(String taskId, int taskStatus) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskTaskModel::getId, taskId);
        FlowTaskTaskModel flowTaskTaskModel = new FlowTaskTaskModel();
        flowTaskTaskModel.setTaskStatus(taskStatus);

        return getModelMapper().filterUpdate(flowTaskTaskModel, condition);
    }

    @Override
    public int batchUpdateTaskStatus(List<String> taskIds, int taskStatus) {
        HumanCondition condition = new HumanCondition();
        condition.filterIn(FlowTaskTaskModel::getId, taskIds.toArray());
        FlowTaskTaskModel flowTaskTaskModel = new FlowTaskTaskModel();
        flowTaskTaskModel.setTaskStatus(taskStatus);

        return getModelMapper().filterUpdate(flowTaskTaskModel, condition);
    }

    @Override
    public List<FlowTaskTaskModel> listByTaskIds(List<String> taskIds) {
        HumanCondition condition = new HumanCondition();
        condition.filterIn(FlowTaskTaskModel::getId, taskIds.toArray());

        return getModelMapper().list(condition);
    }
}