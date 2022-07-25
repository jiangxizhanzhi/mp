package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.service.impl.TargetServiceImpl;
import cn.cyberpeace.mp.auth.service.impl.UserPermissionServiceImpl;
import cn.cyberpeace.mp.auth.service.impl.UserRoleServiceImpl;
import cn.cyberpeace.mp.auth.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/6/9 11:16
 */
@Slf4j
@Service
public class UserCacheService {

    public static final String[] USER_CACHE_VALUES = {
            UserServiceImpl.CACHE_VAL_USERMODEL, UserServiceImpl.CACHE_VAL_USERDTO,
            UserRoleServiceImpl.CACHE_VAL_ROLES,
            UserPermissionServiceImpl.CACHE_VAL_USER_TARGETS};

    public static final String[] TARGET_CACHE_VALUES = {
            TargetServiceImpl.CACHE_VAL_TARGETS};

    @Resource(name = AuthCacheConfig.CACHE_MANAGER_20S)
    private CacheManager cacheManager20s;


    @Resource(name = AuthCacheConfig.CACHE_MANAGER_45S)
    private CacheManager cacheManager45s;


    @Resource(name = AuthCacheConfig.CACHE_MANAGER_10M)
    private CacheManager cacheManager10M;

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
        Cache cache20s = cacheManager20s.getCache(cacheValue);
        Cache cache45s = cacheManager45s.getCache(cacheValue);
        Cache cache10M = cacheManager10M.getCache(cacheValue);
        log.debug("cache20s:{}, cache45s:{}, cache10M:{}", cache20s, cache45s, cache10M);

        if (Objects.nonNull(cache20s)) {
            cache20s.clear();
        }

        if (Objects.nonNull(cache45s)) {
            cache45s.clear();
        }

        if (Objects.nonNull(cache10M)) {
            cache10M.clear();
        }
    }


}
