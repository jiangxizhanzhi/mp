 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CacheUtil.java.
 // File size 627.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.mptool;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName CacheUtil
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class CacheUtil {

    /**
     * 创建定时缓存.
     * Params:
     * timeout – 过期时长，单位：毫秒
     */
    public static Cache<String, String> newTimedCache(long timeout) {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(timeout, TimeUnit.MILLISECONDS).build();
    }

}
