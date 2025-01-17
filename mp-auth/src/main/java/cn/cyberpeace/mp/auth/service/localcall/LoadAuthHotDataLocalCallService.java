 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LoadAuthHotDataLocalCallService.java.
 // File size 2535.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service.localcall;

import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetailsService;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.fields.UserFields;
import cn.cyberpeace.mp.auth.service.impl.UserServiceImpl;
import cn.cyberpeace.mp.auth.service.param.UserQueryParams;
import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@LocalCallServer
public class LoadAuthHotDataLocalCallService {

    @Resource(name = AuthCacheConfig.CACHE_MANAGER_NORMAL)
    private CacheManager cacheManagerNormal;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthUserDetailsService authUserDetailsService;

    /**
     * 加载用户热数据
     */
    @LocalCallFunc
    public void loadHotData() {
        log.info("local-call loadHotData");
        // 加载用户信息
        ListObjServiceResult<UserModel> result = userService.list(new UserQueryParams());
        List<UserModel> userModelList = result.getSource().getObjs();
        Cache cacheById = cacheManagerNormal.getCache(UserServiceImpl.CACHE_VAL_USERMODEL_BY_ID);
        Cache cacheByUserName = cacheManagerNormal.getCache(UserServiceImpl.CACHE_VAL_USERMODEL_BY_USERNAME);
        for (UserModel userModel : userModelList) {
            if (Objects.nonNull(cacheById)) {
                cacheById.put(userModel.getId(), new ObjServiceResult<>(new ObjResult<>(userModel, UserFields.availableFields)));
            }

            if (Objects.nonNull(cacheByUserName)) {
                cacheByUserName.put(userModel.getUsername(), userModel);
            }

            // 加载权限
            authUserDetailsService.loadAuthorities(userModel.getId());
        }
        log.info("local-call loadHotData success!");
    }
}
