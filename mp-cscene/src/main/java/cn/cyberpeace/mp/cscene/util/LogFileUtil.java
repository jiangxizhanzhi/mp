 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogFileUtil.java.
 // File size 1661.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.cscene.dto.logfile.LogFileVO;
import cn.cyberpeace.mp.cscene.service.LogFileService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName LogFileUtil
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class LogFileUtil {


    private static LoadingCache<String, Map<String, List<LogFileVO>>> cache = null;

    /**
     * init
     *
     * @param logFileService
     */
    public static void init(LogFileService logFileService) {
        cache = CacheBuilder.newBuilder()
                .maximumSize(300)
                .expireAfterWrite(360, TimeUnit.SECONDS)
                .build(new CacheLoader<>() {
                    @Override
                    public Map<String, List<LogFileVO>> load(String sceneId) {
                        return logFileService.loadLogFile(sceneId);
                    }
                });
    }

    /**
     * getLogFile
     *
     * @param sceneId
     * @return
     */
    public static Map<String, List<LogFileVO>> getLogFile(String sceneId) {
        try {
            return cache.get(sceneId);
        } catch (Exception e) {
            log.error(String.format("method: getLogFile; params: sceneId=%s; errorMessage: %s", sceneId, e.getMessage()), e);
            return new HashMap<>();
        }
    }
}
