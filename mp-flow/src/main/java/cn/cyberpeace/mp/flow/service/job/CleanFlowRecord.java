package cn.cyberpeace.mp.flow.service.job;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.flow.model.FlowTaskRecordModel;
import cn.cyberpeace.mp.flow.repository.FlowTaskRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Slf4j
@Component
public class CleanFlowRecord {

    @Autowired
    private FlowTaskRecordMapper flowTaskRecordMapper;

    @Scheduled(cron = "0 0 2 * * ?")
    public void execute() {
        log.info("ClearFlowRecord.execute begin.");
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterLessThan(FlowTaskRecordModel::getCreateTime, yesterday);
        Integer num = flowTaskRecordMapper.filterDelete(humanCondition);
        log.info("ClearFlowRecord.execute end. num:{}", num);
    }
}
