package cn.cyberpeace.mp.job.base.schedule;

import cn.cyberpeace.mp.job.base.MethodJobWrapper;
import cn.cyberpeace.mp.job.base.MpJobInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Slf4j
public class MpScheduleJobInfo extends MpJobInfo {

    private static Map<String, MpScheduleJobInfo> TASK_INFOS = new ConcurrentHashMap<>();

    private String cron;

    private int initialDelaySeconds;

    private MethodJobWrapper methodJobWrapper;

    public MpScheduleJobInfo(String taskId, String description, String cron, int initialDelaySeconds, MethodJobWrapper methodJobWrapper) {
        super(taskId, description);
        this.cron = cron;
        this.initialDelaySeconds = initialDelaySeconds;
        this.methodJobWrapper = methodJobWrapper;
    }

    public String getShowInfo() {
        return String.format("id:%s,desc:%s,cron:%s", this.getJobId(), this.getDescription(), cron);
    }

}
