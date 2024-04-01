package cn.cyberpeace.mp.cscene.job;

import cn.cyberpeace.mp.base.job.schedule.MpScheduled;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.service.LogFileService;
import cn.cyberpeace.mp.cscene.util.LogFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class RefreshOriginLogFileJob {

    private ReentrantLock lock = new ReentrantLock();

    private CsceneService csceneService;

    public RefreshOriginLogFileJob(@Autowired CsceneService csceneService, @Autowired LogFileService logFileService) {
        this.csceneService = csceneService;
        LogFileUtil.init(logFileService);
    }


    @MpScheduled(cron = "0 0/5 * * * ?", description = "主动刷新主机原始日志的缓存数据(原始日志实时查询太慢,节点多时性能太差,需要缓存)", id = "refresh_origin_log_file")
    public void action() {
        if (lock.tryLock()) {
            try {
                List<CsceneInstanceModel> cscenes = csceneService.getRunningCscenes();
                // 预热数据
                for (CsceneInstanceModel scene : cscenes) {
                    LogFileUtil.getLogFile(scene.getId());
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
