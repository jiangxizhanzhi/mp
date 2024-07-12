 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UpdateReferenceAndUseStatusScheduleJob.java.
 // File size 889.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.aggregation.service.TargetAggregationService;
import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName UpdateReferenceAndUseStatusScheduleJob
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
@Slf4j
public class UpdateReferenceAndUseStatusScheduleJob {

    @Autowired
    private TargetAggregationService targetAggregationService;

    @MpScheduled(cron = "0 0/5 * * * ?", description = "更新实装的引用以及使用状态", id = "update_entity_reference_and_use_status")
    public void updateReferenceAndUseStatus() {
        targetAggregationService.updateReferenceAndUseStatus();
    }
}
