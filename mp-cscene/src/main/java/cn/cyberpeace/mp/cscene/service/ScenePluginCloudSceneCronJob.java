package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.cscene.service.cron.clearjob.AbstractClearJob;
import cn.cyberpeace.mp.cscene.service.cron.clearjob.ContainerClearJob;
import cn.cyberpeace.mp.cscene.service.cron.clearjob.NetClearJob;
import cn.cyberpeace.mp.cscene.service.cron.clearjob.VmClearJob;
import cn.cyberpeace.mp.cscene.service.cron.clearjob.VrClearJob;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.scene.repository.SceneElementPortMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementPortMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author hewei
 * @Date：2022/3/9 16:37
 */
@Component
@Slf4j
public class ScenePluginCloudSceneCronJob {
    @Autowired
    private SceneElementPortMapper sceneElementPortMapper;

    @Autowired
    private SceneElementMapper sceneElementMapper;

    @Autowired
    private CloudPluginSceneElementPortMapper portMapper;

    @Resource
    private CsceneService csceneService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleNode() {
        log.info("start cloud plugin scene cron job");

        List<AbstractClearJob> jobs = new ArrayList<>() {{
            this.add(new ContainerClearJob(sceneElementMapper));
            this.add(new VmClearJob(sceneElementMapper));
            this.add(new VrClearJob(sceneElementMapper));
            this.add(new NetClearJob(sceneElementMapper));
        }};
        for (AbstractClearJob job : jobs) {
            try {
                job.action();
            } catch (Exception e) {
                log.error("faild to execute clear job {} ", job.getClass().getName(), e);
            }
        }
        log.info("end cloud plugin scene cron job");
    }
}
