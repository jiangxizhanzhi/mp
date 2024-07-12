 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ClearUselessCloudResourcesScheduleJob.java.
 // File size 1102.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.service.cron.clearjob.ClearService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName ClearUselessCloudResourcesScheduleJob
 * @Description
 * @Author hewei
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
@Slf4j
public class ClearUselessCloudResourcesScheduleJob {

    @Resource
    private CsceneAppConfig csceneAppConfig;

    @Resource
    private ClearService clearService;

    @MpScheduled(cron = "0 0/15 * * * ?", description = "清理未正常释放的资源", id = "clear_useless_cloud_resources")
    public void action() {
        if (!Boolean.TRUE.equals(csceneAppConfig.getEnableSceneCronJob())) {
            log.debug("scene cron job is not enable");
            return;
        }
        // 清理未正常释放的资源
        clearService.clearUselessResource();
    }
}
