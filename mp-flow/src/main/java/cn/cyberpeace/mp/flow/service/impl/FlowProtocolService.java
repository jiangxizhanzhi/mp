// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name FlowProtocolServiceImpl.java.
// File size 1925.
// File create date Fri Jul 12 12:55:24 CST 2024.
// File update date Fri Jul 12 12:55:24 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowProtocolModel;
import cn.cyberpeace.mp.flow.repository.FlowProtocolMapper;
import cn.cyberpeace.mp.flow.service.fields.FlowProtocolFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName FlowProtocolServiceImpl
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 */
@Service
@Slf4j
public class FlowProtocolService extends BaseModelServiceImpl<FlowProtocolModel, FlowProtocolMapper, FlowProtocolFields, QueryParams> {

    public FlowProtocolService(FlowProtocolMapper stuMapper, FlowProtocolFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    /**
     * 根据流量id查询
     * @param flowId
     * @return
     */
    public List<FlowProtocolModel> selectByFlowId(String flowId) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual("flowId", flowId);
        return getModelMapper().list(condition);
    }

    public List<String> listFlowProtocols() {
        return getModelMapper().listProtocols();
    }

    /**
     * 根据流量id删除
     * @param flowId
     * @return
     */
    public void deleteByFlowId(String flowId) {
        HumanCondition condition = new HumanCondition();
        condition.filterEqual("flowId", flowId);
        getModelMapper().filterDelete(condition);
    }

}