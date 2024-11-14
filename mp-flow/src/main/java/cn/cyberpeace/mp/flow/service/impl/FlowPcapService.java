 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowPcapServiceImpl.java.
 // File size 1730.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.service.impl;

 import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
 import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
 import cn.cyberpeace.mp.base.service.param.QueryParams;
 import cn.cyberpeace.mp.flow.model.FlowPcapModel;
 import cn.cyberpeace.mp.flow.repository.FlowPcapMapper;
 import cn.cyberpeace.mp.flow.service.fields.FlowPcapFields;
 import lombok.extern.slf4j.Slf4j;
 import org.springframework.stereotype.Service;

 import java.util.List;

/**
 * @ClassName FlowPcapServiceImpl
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 */
@Service
@Slf4j
public class FlowPcapService extends BaseModelServiceImpl<FlowPcapModel, FlowPcapMapper, FlowPcapFields,
        QueryParams>  {

    public FlowPcapService(FlowPcapMapper stuMapper, FlowPcapFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    public int countAll() {
        return getModelMapper().total(new HumanCondition());
    }

    public int deleteAll() {
        return getModelMapper().filterDelete(new HumanCondition());
    }

    public FlowPcapModel getByFlowId(String flowId) {
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(FlowPcapModel::getFlowId, flowId);
        return getModelMapper().first(humanCondition);
    }

    public List<FlowPcapModel> listAll() {
        return getModelMapper().list(new HumanCondition());
    }
}