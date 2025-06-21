// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name FlowTaskTaskServiceImpl.java.
// File size 23287.
// File create date Fri Jul 12 12:55:24 CST 2024.
// File update date Fri Jul 12 12:55:24 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.bo.FlowTaskNode;
import cn.cyberpeace.mp.flow.config.FlowConfig;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowProtocolModel;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.repository.FlowTaskTaskMapper;
import cn.cyberpeace.mp.flow.request.queryparam.FlowTaskPageQueryParams;
import cn.cyberpeace.mp.flow.service.fields.FlowTaskTaskFields;
import cn.cyberpeace.mp.flow.util.CommonUtil;
import cn.cyberpeace.mp.flow.util.ResponseUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName FlowTaskTaskServiceImpl
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 */
@Service
@Slf4j
public class FlowTaskTaskService extends BaseModelServiceImpl<FlowTaskTaskModel, FlowTaskTaskMapper, FlowTaskTaskFields, QueryParams> {

    @Autowired
    private FlowActuatorMapper flowActuatorMapper;

    @Autowired
    private FlowProtocolService flowProtocolService;

    public FlowTaskTaskService(FlowTaskTaskMapper stuMapper, FlowTaskTaskFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    public int updateTaskStatus(String taskId, int taskStatus) {
        HumanCondition condition = new HumanCondition().filterEqual(FlowTaskTaskModel::getId, taskId);
        FlowTaskTaskModel flowTaskTaskModel = new FlowTaskTaskModel();
        flowTaskTaskModel.setTaskStatus(taskStatus);

        return getModelMapper().filterUpdate(flowTaskTaskModel, condition);
    }

    public int batchUpdateTaskStatus(List<String> taskIds, int taskStatus) {
        HumanCondition condition = new HumanCondition().filterIn(FlowTaskTaskModel::getId, taskIds.toArray());
        FlowTaskTaskModel flowTaskTaskModel = new FlowTaskTaskModel();
        flowTaskTaskModel.setTaskStatus(taskStatus);

        return getModelMapper().filterUpdate(flowTaskTaskModel, condition);
    }

    public List<FlowTaskTaskModel> listByTaskIds(List<String> taskIds) {
        HumanCondition condition = new HumanCondition().filterIn(FlowTaskTaskModel::getId, taskIds.toArray());

        return getModelMapper().list(condition);
    }

}