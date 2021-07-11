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


@Slf4j
@Service
public class UserCacheService {

    public static final String[] USER_CACHE_VALUES = {
            UserServiceImpl.CACHE_VAL_USERMODEL_BY_ID, UserServiceImpl.CACHE_VAL_USERMODEL_BY_USERNAME,
            UserServiceImpl.CACHE_VAL_USERDTO, UserServiceImpl.CACHE_VAL_ORGDTOS_TO_USERDTOS,
            AuthUserDetailsService.CACHE_USER_AUTHORITIES,
            TenantUserServiceImpl.CACHE_VAL_ORGS_BY_USERID,
            TenantUserServiceImpl.CACHE_VAL_ORGS_LIST_ALL,
            UserRoleServiceImpl.CACHE_VAL_ROLES,
            UserPermissionServiceImpl.CACHE_VAL_USER_TARGETS};

    public static final String[] TARGET_CACHE_VALUES = {
            TargetServiceImpl.CACHE_VAL_TARGETS,
            RolePermissionModelServiceImpl.CACHE_TARGETS_BY_ROLES};

    @Resource(name = AuthCacheConfig.CACHE_MANAGER_NORMAL)
    private CacheManager cacheManagerNormal;


    @Resource(name = AuthCacheConfig.CACHE_MANAGER_MEDIUM)
    private CacheManager cacheManagerMedium;


    @Resource(name = AuthCacheConfig.CACHE_MANAGER_LONG)
    private CacheManager cacheManagerLong;

    public void evictAllUserCache() {
        log.debug("evict user releated cache...");
        for (String cacheValue : USER_CACHE_VALUES) {
            clean(cacheValue);
        }
    }

    public void evictAllTargetsCache() {
        log.debug("evict targets releated cache...");
        for (String cacheValue : TARGET_CACHE_VALUES) {
            clean(cacheValue);
        }
    }

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
