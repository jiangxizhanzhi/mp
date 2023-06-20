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

@Service
@Slf4j
public class CsceneInstanceUserCountServiceImpl extends BaseModelServiceImpl<CsceneInstanceUserCountModel, CsceneInstanceUserCountMapper, CsceneInstanceUserCountFields,
        QueryParams> implements CsceneInstanceUserCountService {

    public CsceneInstanceUserCountServiceImpl(CsceneInstanceUserCountMapper stuMapper, CsceneInstanceUserCountFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

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

    @Override
    public int countByUserId(String userId) {
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(CsceneInstanceUserCountModel::getUserId, userId);
        return getModelMapper().total(humanCondition);
    }
}