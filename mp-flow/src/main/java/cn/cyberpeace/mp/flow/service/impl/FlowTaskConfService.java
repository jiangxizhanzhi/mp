 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowTaskConfServiceImpl.java.
 // File size 2322.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.service.impl;

 import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
 import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
 import cn.cyberpeace.mp.base.service.param.QueryParams;
 import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;
 import cn.cyberpeace.mp.flow.repository.FlowTaskConfMapper;
 import cn.cyberpeace.mp.flow.service.fields.FlowTaskConfFields;
 import lombok.extern.slf4j.Slf4j;
 import org.apache.commons.collections4.CollectionUtils;
 import org.springframework.stereotype.Service;

 import java.util.List;

/**
 * @ClassName FlowTaskConfServiceImpl
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 */
@Service
@Slf4j
public class FlowTaskConfService extends BaseModelServiceImpl<FlowTaskConfModel, FlowTaskConfMapper, FlowTaskConfFields,
        QueryParams>   {

    public FlowTaskConfService(FlowTaskConfMapper stuMapper, FlowTaskConfFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    /**
     * 根据taskId查询配置信息
     * @param taskId
     * @return
     */
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

    public List<FlowTaskConfModel> listByTaskIds(List<String> taskIds) {
        HumanCondition condition = new HumanCondition();
        condition.filterIn(FlowTaskConfModel::getTaskId, taskIds.toArray());

        return getModelMapper().list(condition);
    }
}