package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
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
public class RefreshCacheAndDbLimitNodesScheduleJob {
    @Resource
    private CsceneAppConfig csceneAppConfig;

    @Resource
    private CsceneService csceneService;

    @MpScheduled(cron = "0 0 2 * * ?", description = "内存和数据库中节点数量保持同步", id = "refresh_cache_and_db_limit_nodes")
    public void action() {
        if (!Boolean.TRUE.equals(csceneAppConfig.getEnableSceneCronJob())) {
            log.debug("scene cron job is not enable");
            return;
        }
        try {
            NodeLimitUtils.getAndRefreshCount();
        } catch (Exception e) {
            log.error("failed to refresh node limit count", e);
        }
    }
}
