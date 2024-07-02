package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.service.CsceneSnapshotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName CorrectSnapshotRelativeInfoScheduleJob
 * @Description
 * @Author hewei
 * @Date 2022/3/9 16:37
 * @Version 1.0.0
 */
@Component
@Slf4j
public class CorrectSnapshotRelativeInfoScheduleJob {

    @Resource
    private CsceneAppConfig csceneAppConfig;

    @Resource
    private CsceneSnapshotService csceneSnapshotService;

    @MpScheduled(cron = "0 0 0/1 * * ?", description = "纠正cscene和scene快照表的数据", id = "correct_snapshot_relative_info")
    public void action() {
        if (!Boolean.TRUE.equals(csceneAppConfig.getEnableSceneCronJob())) {
            log.debug("scene cron job is not enable");
            return;
        }
        // 纠正cscene和scene快照表的数据
        try {
            csceneSnapshotService.correctSnapshotRelativeInfo();
        } catch (Exception e) {
            log.error("failed to corrcet snapshot relative info", e);
        }

    }
}
