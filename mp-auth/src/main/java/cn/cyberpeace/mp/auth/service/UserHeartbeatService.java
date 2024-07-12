 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserHeartbeatService.java.
 // File size 876.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.UserHeartbeatModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @ClassName UserHeartbeatService
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface UserHeartbeatService extends BaseModelService<UserHeartbeatModel, QueryParams> {

    /**
     * heartbeat
     */
    void heartbeat();

    /**
     * getHeartbeatMap
     *
     * @return
     */
    ConcurrentHashMap<UserModel, CopyOnWriteArrayList<LocalDateTime>> getHeartbeatMap();

}
