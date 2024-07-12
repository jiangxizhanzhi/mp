 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserHeartbeatServiceImpl.java.
 // File size 2579.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.UserHeartbeatModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.UserHeartbeatMapper;
import cn.cyberpeace.mp.auth.service.UserHeartbeatService;
import cn.cyberpeace.mp.auth.service.fields.UserHeartbeatFields;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @ClassName UserHeartbeatServiceImpl
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 * @see UserHeartbeatService
 */
@Service
@Slf4j
public class UserHeartbeatServiceImpl extends BaseModelServiceImpl<UserHeartbeatModel, UserHeartbeatMapper, UserHeartbeatFields, QueryParams> implements UserHeartbeatService {

    private static ConcurrentHashMap<UserModel, CopyOnWriteArrayList<LocalDateTime>> heartbeatMap = new ConcurrentHashMap<>();


    /**
     * UserHeartbeatServiceImpl
     *
     * @param modelMapper
     * @param modelFields
     */
    @Autowired
    public UserHeartbeatServiceImpl(UserHeartbeatMapper modelMapper, UserHeartbeatFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    /**
     * heartbeat
     */
    @Override
    public void heartbeat() {
        UserModel userModel = UserUtils.getLoginUser();
        if (Objects.nonNull(userModel)) {
            CopyOnWriteArrayList<LocalDateTime> timestampList = heartbeatMap.get(userModel);
            if (Objects.nonNull(timestampList)) {
                timestampList.add(LocalDateTime.now());
            } else {
                timestampList = new CopyOnWriteArrayList();
                timestampList.add(LocalDateTime.now());
                heartbeatMap.put(userModel, timestampList);
            }
        } else {
            log.error("not existed illegal logined user!");
        }
    }

    /**
     * getHeartbeatMap
     *
     * @return
     */
    @Override
    public ConcurrentHashMap<UserModel, CopyOnWriteArrayList<LocalDateTime>> getHeartbeatMap() {
        return heartbeatMap;
    }
}
