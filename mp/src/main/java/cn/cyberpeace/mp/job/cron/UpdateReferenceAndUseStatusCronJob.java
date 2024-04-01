package cn.cyberpeace.mp.job.cron;

import cn.cyberpeace.mp.aggregation.service.TargetAggregationService;
import cn.cyberpeace.mp.base.job.schedule.MpScheduled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdateReferenceAndUseStatusCronJob {

    @Autowired
    private TargetAggregationService targetAggregationService;

    @MpScheduled(cron = "0 0/5 * * * ?", description = "更新实装的引用以及使用状态", id = "update_entity_reference_and_use_status")
    public void updateReferenceAndUseStatus() {
        targetAggregationService.updateReferenceAndUseStatus();
    }
}
