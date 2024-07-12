 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneInstanceUserCountServiceImpl.java.
 // File size 2875.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceUserCountModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceUserCountMapper;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceUserCountService;
import cn.cyberpeace.mp.cscene.service.fields.CsceneInstanceUserCountFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName CsceneInstanceUserCountServiceImpl
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 * @see CsceneInstanceUserCountService
 */
@Service
@Slf4j
public class CsceneInstanceUserCountServiceImpl extends BaseModelServiceImpl<CsceneInstanceUserCountModel, CsceneInstanceUserCountMapper, CsceneInstanceUserCountFields,
        QueryParams> implements CsceneInstanceUserCountService {

    /**
     * CsceneInstanceUserCountServiceImpl
     *
     * @param stuMapper
     * @param stuFields
     */
    public CsceneInstanceUserCountServiceImpl(CsceneInstanceUserCountMapper stuMapper, CsceneInstanceUserCountFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    /**
     * increaseCount
     *
     * @param userId
     */
    @Override
    public void increaseCount(String userId) {
        if (countByUserId(userId) == 0) {
            CsceneInstanceUserCountModel userCountModel = new CsceneInstanceUserCountModel();
            userCountModel.setUserId(userId);
            userCountModel.setCount(1);
            getModelMapper().create(userCountModel);
        } else {
            getModelMapper().increaseCount(userId);
        }
    }

    /**
     * decreaseCount
     *
     * @param userId
     */
    @Override
    public void decreaseCount(String userId) {
        if (countByUserId(userId) == 0) {
            log.warn("decrease user cscene count error for user info not exist:{}", userId);
            CsceneInstanceUserCountModel userCountModel = new CsceneInstanceUserCountModel();
            userCountModel.setUserId(userId);
            userCountModel.setCount(0);
            getModelMapper().create(userCountModel);
        } else {
            getModelMapper().decreaseCount(userId);
        }
    }

    /**
     * countByUserId
     *
     * @param userId
     * @return int
     */
    @Override
    public int countByUserId(String userId) {
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(CsceneInstanceUserCountModel::getUserId, userId);
        return getModelMapper().total(humanCondition);
    }
}