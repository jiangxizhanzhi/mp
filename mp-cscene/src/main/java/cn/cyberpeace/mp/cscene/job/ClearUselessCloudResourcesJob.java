package cn.cyberpeace.mp.cscene.job;

import cn.cyberpeace.mp.base.cron.MpScheduled;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.service.cron.clearjob.ClearService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description:
 * @Author hewei
 * @Date：2022/3/9 16:37
 */
@Component
@Slf4j
public class ClearUselessCloudResourcesJob {

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
