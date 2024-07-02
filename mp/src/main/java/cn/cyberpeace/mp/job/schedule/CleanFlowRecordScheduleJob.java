package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.flow.model.FlowTaskRecordModel;
import cn.cyberpeace.mp.flow.repository.FlowTaskRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @ClassName CleanFlowRecordScheduleJob
 * @Description
 * @Author guquan
 * @Date 2023/6/27 16:22
 * @Version 1.0.0
 */
@Slf4j
@Component
public class CleanFlowRecordScheduleJob {

    @Autowired
    private FlowTaskRecordMapper flowTaskRecordMapper;

    @MpScheduled(cron = "0 0 2 * * ?", description = "删除无效的流量发生任务", id = "clean_flow_record")
    public void execute() {
        log.info("ClearFlowRecord.execute begin.");
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterLessThan(FlowTaskRecordModel::getCreateTime, yesterday);
        Integer num = flowTaskRecordMapper.filterDelete(humanCondition);
        log.info("ClearFlowRecord.execute end. num:{}", num);
    }
}
