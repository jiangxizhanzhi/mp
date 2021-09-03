package cn.cyberpeace.mp.situationaware.task;

import cn.cyberpeace.mp.situationaware.repository.ProbeModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
  *@Desciption probe探针的定时任务，用于监控探针的状态
  *@Author zhangjin
  *@Date 9/3/21 2:25 下午
**/
@Component
public class ProbeManagerTask {

    @Autowired
    private ProbeModelMapper probeModelMapper;

    @Scheduled(fixedRate = 1000*2)
    private void processProbeStatus() {
        probeModelMapper.queryAllProbes();
    }
}
