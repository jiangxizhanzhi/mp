package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowTaskRecordModel;
import cn.cyberpeace.mp.flow.repository.FlowTaskRecordMapper;
import cn.cyberpeace.mp.flow.service.FlowTaskRecordService;
import cn.cyberpeace.mp.flow.service.fields.FlowTaskRecordFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FlowTaskRecordServiceImpl extends BaseModelServiceImpl<FlowTaskRecordModel, FlowTaskRecordMapper, FlowTaskRecordFields,
        QueryParams> implements FlowTaskRecordService {

    public FlowTaskRecordServiceImpl(FlowTaskRecordMapper stuMapper, FlowTaskRecordFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    /**
     * 根据taskId查询流量任务记录
     * @param taskId
     * @return
     */
    @Override
    public List<FlowTaskRecordModel> findByTaskId(String taskId) {
        log.info("FlowTaskRecordServiceImpl.findByTaskId begin. taskId:{}", taskId);
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskRecordModel::getTaskId, taskId);
        condition.orderBy("-createTime");
        List<FlowTaskRecordModel> flowTaskConfModelList = getModelMapper().list(condition);
        log.info("FlowTaskRecordServiceImpl.findByTaskId end. flowTaskConfModelList:{}", flowTaskConfModelList);
        return flowTaskConfModelList;
    }

    /**
     * 根据taskId删除
     * @param taskId
     */
    @Override
    public void deleteByTaskId(String taskId) {
        log.info("FlowTaskRecordServiceImpl.deleteByTaskId begin. taskId:{}", taskId);
        HumanCondition condition = new HumanCondition();
        condition.filterEqual(FlowTaskRecordModel::getTaskId, taskId);
        getModelMapper().filterDelete(condition);
        log.info("FlowTaskRecordServiceImpl.deleteByTaskId end.");
    }

}