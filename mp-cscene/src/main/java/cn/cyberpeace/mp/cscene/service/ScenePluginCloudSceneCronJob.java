package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.cscene.SceneCronService;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.service.cron.clearjob.ClearService;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description:
 * @Author hewei
 * @Date：2022/3/9 16:37
 */
@Component
@Slf4j
public class ScenePluginCloudSceneCronJob {
    @Resource
    private CsceneService csceneService;

    @Resource
    private SceneCronService cronService;

    @Resource
    private CsceneAppConfig csceneAppConfig;

    @Resource
    private ClearService clearService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleNode() {
        if (!Boolean.TRUE.equals(csceneAppConfig.getEnableSceneCronJob())) {
            log.info("scene cron job is not enable");
            return;
        }
        log.info("start cloud plugin scene cron job");

        try {
            int count = csceneService.getActiveVirtualDeviceCount();
            NodeLimitUtils.refreshCount(count);
        } catch (Exception e) {
            log.error("failed to refresh node limit count", e);
        }

        try {
            cronService.correctErrorStatusScene();
        } catch (Exception e) {
            log.error("failed to correct error status scene", e);
        }

        clearService.clearUselessResource();
        log.info("end cloud plugin scene cron job");
    }
}
