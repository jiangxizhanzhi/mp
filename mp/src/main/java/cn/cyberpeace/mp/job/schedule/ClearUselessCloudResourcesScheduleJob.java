package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.service.HardwareLimitRefreshService;
import cn.cyberpeace.mp.cscene.service.cron.clearjob.ClearService;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
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
public class ClearUselessCloudResourcesScheduleJob {

    @Resource
    private CsceneAppConfig csceneAppConfig;

    @Resource
    private ClearService clearService;

    @Resource
    private CsceneService csceneService ;

    @Resource
    private HardwareLimitRefreshService hardwareLimitRefreshService ;

    @MpScheduled(cron = "0 0/15 * * * ?", description = "清理未正常释放的资源", id = "clear_useless_cloud_resources")
    public void action() {
        if (!Boolean.TRUE.equals(csceneAppConfig.getEnableSceneCronJob())) {
            log.debug("scene cron job is not enable");
            return;
        }
        // 清理未正常释放的资源
        clearService.clearUselessResource();


        try {
            hardwareLimitRefreshService.init();
        }catch (Exception e){
            log.error("failed to refresh hardware limit count", e);
        }

    }
}
