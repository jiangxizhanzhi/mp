package cn.cyberpeace.mp.cscene.init.jobs;

import cn.cyberpeace.mp.cscene.dto.logfile.LogFileVO;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.service.LogFileService;
import cn.cyberpeace.mp.cscene.util.LogFileUtil;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class LogFileJob {

    private volatile boolean refreshing = false;

    private LoadingCache<String, Map<String, List<LogFileVO>>> cache = null;

    private CsceneService csceneService;

    public LogFileJob(@Autowired CsceneService csceneService,
                      @Autowired LogFileService logFileService
    ) {
        this.csceneService = csceneService;
        LogFileUtil.init(logFileService);
    }


    @Scheduled(cron = "0 0/5 * * * ?")
    public void action() {
        if (refreshing) {
            return;
        }
        List<CsceneInstanceModel> cscenes = csceneService.getRunningCscenes();
        // 预热数据
        for (CsceneInstanceModel scene : cscenes) {
            try {
                cache.refresh(scene.getId());
            } catch (Exception e) {
                log.error("failed to load log file , scene id is " + scene.getScene(), e);
            }
        }
        refreshing = false;
    }
}
