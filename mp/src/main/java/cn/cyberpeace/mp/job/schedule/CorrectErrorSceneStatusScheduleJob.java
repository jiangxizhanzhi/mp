package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import cn.cyberpeace.mp.cscene.SceneCorrectService;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
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
public class CorrectErrorSceneStatusScheduleJob {

    @Resource
    private CsceneAppConfig csceneAppConfig;

    @Resource
    private SceneCorrectService sceneCorrectService;

    @MpScheduled(cron = "0 0/15 * * * ?", description = "启动耗时很长的场景,状态置为异常", id = "correct_error_scene_status")
    public void action() {
        if (!Boolean.TRUE.equals(csceneAppConfig.getEnableSceneCronJob())) {
            log.debug("scene cron job is not enable");
            return;
        }
        // 启动耗时很长的场景,状态置为异常
        try {
            sceneCorrectService.correctErrorStatusScene();
        } catch (Exception e) {
            log.error("failed to correct error status scene", e);
        }
    }
}
