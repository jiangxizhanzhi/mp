package cn.cyberpeace.mp.cscene.init.jobs;

import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.service.LogFileService;
import cn.cyberpeace.mp.cscene.util.LogFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LogFileJob {

    private volatile boolean refreshing = false;

    private CsceneService csceneService;

    public LogFileJob(@Autowired CsceneService csceneService, @Autowired LogFileService logFileService) {
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
            LogFileUtil.getLogFile(scene.getId());
        }
        refreshing = false;
    }
}
