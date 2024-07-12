 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneInstanceUserCountService.java.
 // File size 932.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceUserCountModel;

/**
 * @ClassName CsceneInstanceUserCountService
 * @Description 用户场景统计service
 * @Author qiaokang
 * @Date 2023-06-20 14:26:35
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface CsceneInstanceUserCountService extends BaseModelService<CsceneInstanceUserCountModel, QueryParams> {

    /**
     * 增加用户启动场景数
     *
     * @param userId
     */
    void increaseCount(String userId);

    /**
     * 减少用户启动场景数
     *
     * @param userId
     */
    void decreaseCount(String userId);

    /**
     * 统计用户数据
     *
     * @param userId
     * @return
     */
    int countByUserId(String userId);

}

