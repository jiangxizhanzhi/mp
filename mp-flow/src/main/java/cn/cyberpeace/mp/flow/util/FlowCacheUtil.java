 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowCacheUtil.java.
 // File size 448.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @Description: 流量缓存工具类
 * @Author qiaokang
 * @Date：2022/11/16 11:28
 */
public class FlowCacheUtil {
    /**
     * pcap包解析缓存
     */
    public static final Cache<String, String> pcapAnalyseCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

}
