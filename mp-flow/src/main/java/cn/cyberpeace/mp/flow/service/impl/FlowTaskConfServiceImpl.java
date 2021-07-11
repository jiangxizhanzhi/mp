package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;
import cn.cyberpeace.mp.flow.repository.FlowTaskConfMapper;
import cn.cyberpeace.mp.flow.service.FlowTaskConfService;
import cn.cyberpeace.mp.flow.service.fields.FlowTaskConfFields;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FlowTaskConfServiceImpl extends BaseModelServiceImpl<FlowTaskConfModel, FlowTaskConfMapper, FlowTaskConfFields,
        QueryParams> implements FlowTaskConfService {

    public FlowTaskConfServiceImpl(FlowTaskConfMapper stuMapper, FlowTaskConfFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    
    @Override
    public FlowTaskConfModel findByTaskId(String taskId) {
        log.info("FlowTaskConfServiceImpl.findByTaskId begin. taskId:{}", taskId);
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskConfModel::getTaskId, taskId);
        List<FlowTaskConfModel> flowTaskConfModelList = getModelMapper().list(condition);
        if (CollectionUtils.isNotEmpty(flowTaskConfModelList)) {
            FlowTaskConfModel result = flowTaskConfModelList.get(0);
            log.info("FlowTaskConfServiceImpl.findByTaskId end. result:{}", result);
            return result;
        }
        log.info("FlowTaskConfServiceImpl.findByTaskId end.");
        return null;
    }

    @Override
    public List<FlowTaskConfModel> listByTaskIds(List<String> taskIds) {
        HumanCondition condition = new HumanCondition();
        condition.filterIn(FlowTaskConfModel::getTaskId, taskIds.toArray());

        return getModelMapper().list(condition);
    }
}