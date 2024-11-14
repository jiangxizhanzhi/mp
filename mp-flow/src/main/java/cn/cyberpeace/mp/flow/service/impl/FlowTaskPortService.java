// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name FlowTaskPortServiceImpl.java.
// File size 2292.
// File create date Fri Jul 12 12:55:24 CST 2024.
// File update date Fri Jul 12 12:55:24 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.FlowTaskPortModel;
import cn.cyberpeace.mp.flow.repository.FlowTaskPortMapper;
import cn.cyberpeace.mp.flow.service.fields.FlowTaskPortFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName FlowTaskPortServiceImpl
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 */
@Service
@Slf4j
public class FlowTaskPortService extends BaseModelServiceImpl<FlowTaskPortModel, FlowTaskPortMapper, FlowTaskPortFields, QueryParams> {

    public FlowTaskPortService(FlowTaskPortMapper stuMapper, FlowTaskPortFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }
}