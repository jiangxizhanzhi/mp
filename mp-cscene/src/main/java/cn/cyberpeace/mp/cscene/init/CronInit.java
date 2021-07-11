package cn.cyberpeace.mp.cscene.init;

import cn.cyberpeace.mp.cscene.init.jobs.SceneNodeNumInitJob;
import cn.cyberpeace.mp.sceneplugincloud.init.jobs.AbstractInitJob;
import cn.cyberpeace.mp.sceneplugincloud.init.jobs.CloudInstanceLinkInitJob;
import cn.cyberpeace.mp.sceneplugincloud.init.jobs.CloudLocalFileMappingInitJob;
import cn.cyberpeace.mp.sceneplugincloud.init.jobs.CloudMessageConvertInitJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class CronInit {

    @Resource
    private CloudInstanceLinkInitJob cloudInstanceLinkInitJob;

    @Resource
    private CloudMessageConvertInitJob cloudMessageConvertInitJob;

    @Resource
    private CloudLocalFileMappingInitJob cloudLocalFileMappingInitJob;

    @Resource
    private SceneNodeNumInitJob sceneNodeNumInitJob;

    @Scheduled(cron = "0 0/15 * * * ?")
    public void execute() {
        List<AbstractInitJob> jobs = new ArrayList<>();
        jobs.add(cloudInstanceLinkInitJob);
        jobs.add(cloudMessageConvertInitJob);
        jobs.add(cloudLocalFileMappingInitJob);
        jobs.add(sceneNodeNumInitJob);
        for (var job : jobs) {
            try {
                job.executed();
            } catch (Exception e) {
                log.error("failed to execute job : {}", job.getClass().getName(), e);
            }
        }
    }
}
