package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.cscene.service.cron.clearjob.AbstractClearJob;
import cn.cyberpeace.mp.cscene.service.cron.clearjob.ContainerClearJob;
import cn.cyberpeace.mp.cscene.service.cron.clearjob.NetClearJob;
import cn.cyberpeace.mp.cscene.service.cron.clearjob.VmClearJob;
import cn.cyberpeace.mp.cscene.service.cron.clearjob.VrClearJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    private final ContainerClearJob containerClearJob = new ContainerClearJob();
    private final VmClearJob vmClearJob = new VmClearJob();
    private final NetClearJob netClearJob = new NetClearJob();
    private final VrClearJob vrClearJob = new VrClearJob();

//    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleNode() {
        List<AbstractClearJob> jobs = new ArrayList<>() {{
            this.add(containerClearJob);
            this.add(vmClearJob);
            this.add(vrClearJob);
            this.add(netClearJob);
        }};
        for (AbstractClearJob job : jobs) {
            try {
                job.action();
            } catch (Exception e) {
                log.error("faild to execute clear job {} ", job.getClass().getName(), e);
            }
        }
    }
}
