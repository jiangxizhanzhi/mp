package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceUserCountModel;

/**
 * @Description 用户场景统计service
 * @Author qiaokang
 * @Date 2023-06-20 14:26:35
 */
public interface CsceneInstanceUserCountService extends BaseModelService<CsceneInstanceUserCountModel, QueryParams> {

    /**
     * 增加用户启动场景数
     * @param userId
     */
    void increaseCount(String userId);

    /**
     * 减少用户启动场景数
     * @param userId
     */
    void decreaseCount(String userId);

    /**
     * 统计用户数据
     * @param userId
     * @return
     */
    int countByUserId(String userId);

}

