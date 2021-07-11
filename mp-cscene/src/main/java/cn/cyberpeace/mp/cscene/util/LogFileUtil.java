package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.cscene.dto.logfile.LogFileVO;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.service.LogFileService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LogFileUtil {

    private static volatile boolean refreshing = false;

    private static LoadingCache<String, Map<String, List<LogFileVO>>> cache = null;

    private static ScheduledThreadPoolExecutor refreshExecutor = new ScheduledThreadPoolExecutor(1);

    public static void init() {
        if (cache == null) {
            synchronized (LogFileUtil.class) {
                if (cache == null) {
                    cache = CacheBuilder.newBuilder().maximumSize(300).expireAfterWrite(360, TimeUnit.SECONDS).build(new CacheLoader<>() {
                        @Override
                        public Map<String, List<LogFileVO>> load(String sceneId) {
                            return loadLogFile(sceneId);
                        }
                    });
                    refreshExecutor.scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {
                            if (refreshing) {
                                return;
                            }
                            refreshing = true;
                            
                            var csceneService = BeanCommon.getBean(CsceneService.class);
                            List<CsceneInstanceModel> cscenes = csceneService.getRunningCscenes();
                            
                            for (CsceneInstanceModel scene : cscenes) {
                                try {
                                    cache.refresh(scene.getId());
                                } catch (Exception e) {
                                    log.error("failed to load log file , scene id is " + scene.getScene(), e);
                                }
                            }
                            refreshing = false;
                        }
                    }, 1,300, TimeUnit.SECONDS);
                }
            }
        }
    }

    public static Map<String, List<LogFileVO>> getLogFile(String sceneId) {
        init();
        return cache.getUnchecked(sceneId);
    }

    public static Map<String, List<LogFileVO>> loadLogFile(String sceneId) {
        init();
        return BeanCommon.getBean(LogFileService.class).loadLogFile(sceneId);
    }
}
