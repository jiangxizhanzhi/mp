// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name FlowScriptParamServiceImpl.java.
// File size 1708.
// File create date Fri Jul 12 12:55:24 CST 2024.
// File update date Fri Jul 12 12:55:24 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import cn.cyberpeace.mp.flow.repository.FlowScriptParamMapper;
import cn.cyberpeace.mp.flow.service.field.FlowScriptParamFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName FlowScriptParamServiceImpl
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 */
@Service
@Slf4j
public class FlowScriptParamService extends BaseModelServiceImpl<FlowScriptParamModel, FlowScriptParamMapper, FlowScriptParamFields, QueryParams> {

    public FlowScriptParamService(FlowScriptParamMapper stuMapper, FlowScriptParamFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    public List<FlowScriptParamModel> selectByFlowId(String flowId) {
        return getModelMapper().selectByFlowId(flowId);
    }

    public List<FlowScriptParamModel> selectByFlowIds(List<String> flowIds) {
        return getModelMapper().selectByFlowIds(flowIds);
    }

    public int deleteByFlowId(String flowId) {
        return getModelMapper().deleteByFlowId(flowId);
    }

    public int deleteByFlowIds(List<String> flowIds) {
        return getModelMapper().deleteByFlowIds(flowIds);
    }

}