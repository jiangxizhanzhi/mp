package cn.cyberpeace.mp.aggregation.task;

import cn.cyberpeace.mp.aggregation.service.TargetAggregationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TargetTask {

    @Autowired
    private TargetAggregationService targetAggregationService;

    @Scheduled(fixedRate = 1000 * 60 * 5)
    private void processProbeStatus() {
        log.info("====>>>>>>updateReferenceAndUseStatus task start<<<<<========");
        targetAggregationService.updateReferenceAndUseStatus();
        log.info("====>>>>>>updateReferenceAndUseStatus task end<<<<<========");
    }
}
