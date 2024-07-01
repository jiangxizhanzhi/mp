package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.UserHeartbeatModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
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
