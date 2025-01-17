 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserCacheService.java.
 // File size 2592.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetailsService;
import cn.cyberpeace.mp.auth.service.impl.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

 /**
  * 用户缓存服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
@Service
public class UserCacheService {

     /**
      * 用户缓存值
      */
     public static final String[] USER_CACHE_VALUES = {
            UserServiceImpl.CACHE_VAL_USERMODEL_BY_ID, UserServiceImpl.CACHE_VAL_USERMODEL_BY_USERNAME,
            UserServiceImpl.CACHE_VAL_USERDTO, UserServiceImpl.CACHE_VAL_ORGDTOS_TO_USERDTOS,
            AuthUserDetailsService.CACHE_USER_AUTHORITIES,
            TenantUserServiceImpl.CACHE_VAL_ORGS_BY_USERID,
            TenantUserServiceImpl.CACHE_VAL_ORGS_LIST_ALL,
            UserRoleServiceImpl.CACHE_VAL_ROLES,
            UserPermissionServiceImpl.CACHE_VAL_USER_TARGETS};

     /**
      *
      */
     public static final String[] TARGET_CACHE_VALUES = {
            TargetServiceImpl.CACHE_VAL_TARGETS,
            RolePermissionModelServiceImpl.CACHE_TARGETS_BY_ROLES};

     /**
      *
      */
     @Resource(name = AuthCacheConfig.CACHE_MANAGER_NORMAL)
    private CacheManager cacheManagerNormal;

     /**
      *
      */
     @Resource(name = AuthCacheConfig.CACHE_MANAGER_MEDIUM)
    private CacheManager cacheManagerMedium;

     /**
      *
      */
     @Resource(name = AuthCacheConfig.CACHE_MANAGER_LONG)
    private CacheManager cacheManagerLong;

     /**
      * 清空所有用户缓存
      */
     public void evictAllUserCache() {
        log.debug("evict user releated cache...");
        for (String cacheValue : USER_CACHE_VALUES) {
            clean(cacheValue);
        }
    }

     /**
      * 退出所有目标缓存
      */
     public void evictAllTargetsCache() {
        log.debug("evict targets releated cache...");
        for (String cacheValue : TARGET_CACHE_VALUES) {
            clean(cacheValue);
        }
    }

     /**
      * 清洁
      *
      * @param cacheValue 缓存值
      */
     private void clean(String cacheValue) {
        Cache cacheNormal = cacheManagerNormal.getCache(cacheValue);
        Cache cacheMedium = cacheManagerMedium.getCache(cacheValue);
        Cache cacheLong = cacheManagerLong.getCache(cacheValue);

        if (Objects.nonNull(cacheNormal)) {
            cacheNormal.clear();
        }

        if (Objects.nonNull(cacheMedium)) {
            cacheMedium.clear();
        }

        if (Objects.nonNull(cacheLong)) {
            cacheLong.clear();
        }
    }


}
