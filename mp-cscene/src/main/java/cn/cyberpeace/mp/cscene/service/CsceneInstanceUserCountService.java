package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceUserCountModel;


public interface CsceneInstanceUserCountService extends BaseModelService<CsceneInstanceUserCountModel, QueryParams> {

    
    void increaseCount(String userId);

    
    void decreaseCount(String userId);

    
    int countByUserId(String userId);

}

