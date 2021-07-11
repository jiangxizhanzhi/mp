package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.cscene.SceneCorrectService;
import cn.cyberpeace.mp.cscene.service.cron.clearjob.ClearService;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.GregorianCalendar;


@Component
@Slf4j
public class ScenePluginCloudSceneCronService {
    @Resource
    private CsceneService csceneService;

    @Resource
    private SceneCorrectService sceneCorrectService;

    @Resource
    private ClearService clearService;

    @Resource
    private CsceneSnapshotService csceneSnapshotService;

    public void action() {
        log.info("start cloud plugin scene correct job");

        
        try {
            GregorianCalendar calendar = new GregorianCalendar();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if (hour < 2) {
                int count = csceneService.getActiveVirtualDeviceCount();
                NodeLimitUtils.refreshCount(count);
            }
        } catch (Exception e) {
            log.error("failed to refresh node limit count", e);
        }

        
        try {
            sceneCorrectService.correctErrorStatusScene();
        } catch (Exception e) {
            log.error("failed to correct error status scene", e);
        }

        
        try {
            csceneSnapshotService.correctSnapshotRelativeInfo();
        } catch (Exception e) {
            log.error("failed to corrcet snapshot relative info", e);
        }

        
        clearService.clearUselessResource();
        log.info("end cloud plugin scene correct job");
    }
}
