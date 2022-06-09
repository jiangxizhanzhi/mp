package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
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

    public static final String[] CACHE_VALUES = {
            UserServiceImpl.CACHE_VAL_USERMODEL, UserServiceImpl.CACHE_VAL_USERDTO,
            UserRoleServiceImpl.CACHE_VAL_ROLES,
            UserPermissionServiceImpl.CACHE_VAL_USER_TARGETS};

    @Resource(name = AuthCacheConfig.CACHE_MANAGER_20S)
    private CacheManager cacheManager20s;


    @Resource(name = AuthCacheConfig.CACHE_MANAGER_45S)
    private CacheManager cacheManager45s;

    //@CacheEvict(cacheManager = AuthCacheConfig.CACHE_MANAGER_20S,
    //        value = {UserServiceImpl.CACHE_VAL_USERMODEL, UserServiceImpl.CACHE_VAL_USERDTO,
    //                UserRoleServiceImpl.CACHE_VAL_ROLES,
    //                UserPermissionServiceImpl.CACHE_VAL_USER_TARGETS},
    //        allEntries = true)
    public void evictAllUserCache() {
        log.info("evict user releated cache...");

        for (String cacheValue : CACHE_VALUES) {
            clean(cacheValue);
        }
    }

    private void clean(String cacheValue) {
        Cache cache1 = cacheManager20s.getCache(cacheValue);
        if (Objects.nonNull(cache1)) {
            cache1.clear();
        }

        Cache cache2 = cacheManager45s.getCache(cacheValue);
        if (Objects.nonNull(cache2)) {
            cache2.clear();
        }
    }


}
