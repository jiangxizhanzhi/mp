 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EsCollectionDataStorageServiceImpl.java.
 // File size 2398.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.config.FlowConfig;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.model.EsCollectionDataStorageModel;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.repository.EsCollectionDataStorageMapper;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.service.EsCollectionDataStorageService;
import cn.cyberpeace.mp.flow.service.FlowActuatorService;
import cn.cyberpeace.mp.flow.service.field.EsCollectionDataStorageFields;
import cn.cyberpeace.mp.flow.service.field.FlowActuatorFields;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName EsCollectionDataStorageServiceImpl
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 * @see EsCollectionDataStorageService
 */
@Service
@Slf4j
public class EsCollectionDataStorageServiceImpl extends BaseModelServiceImpl<EsCollectionDataStorageModel, EsCollectionDataStorageMapper,
        EsCollectionDataStorageFields, QueryParams> implements EsCollectionDataStorageService {

    public EsCollectionDataStorageServiceImpl(EsCollectionDataStorageMapper stuMapper, EsCollectionDataStorageFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    @Override
    public EsCollectionDataStorageModel getByDateAndChannel(String date, String channel) {
        return getModelMapper().first(
                new HumanCondition().filterEqual(EsCollectionDataStorageModel::getDate, date)
                        .filterEqual(EsCollectionDataStorageModel::getChannel, channel));
    }

    @Override
    public List<EsCollectionDataStorageModel> listByDate(String date) {
        return getModelMapper().listByDate(date);
    }
}
