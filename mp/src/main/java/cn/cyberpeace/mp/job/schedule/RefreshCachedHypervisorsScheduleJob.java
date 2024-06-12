package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.CloudHypervisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RefreshCachedHypervisorsScheduleJob {

    @MpScheduled(cron = "0 0/2 * * * ?", id = "refresh_cached_hypervisors", description = "刷新缓存的Hypervisors")
    public void action() {
        CloudHypervisor.refreshCachedHypervisors();
    }

}
