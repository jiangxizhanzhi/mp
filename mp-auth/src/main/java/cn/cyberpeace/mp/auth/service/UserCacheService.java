package cn.cyberpeace.mp.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/6/9 11:16
 */
@Slf4j
@Service
public class UserCacheService {

    //@CacheEvict(cacheNames = {AuthCacheConfig.CACHE_MANAGER_20S, AuthCacheConfig.CACHE_MANAGER_45S},
    //        value = {UserServiceImpl.CACHE_VAL_USERMODEL, UserServiceImpl.CACHE_VAL_USERDTO,
    //                UserRoleServiceImpl.CACHE_VAL_ROLES,
    //                UserPermissionServiceImpl.CACHE_VAL_USER_TARGETS},
    //        allEntries = true)
    public void evictAllUserCache() {
        log.info("evict user releated cache...");
    }

}
