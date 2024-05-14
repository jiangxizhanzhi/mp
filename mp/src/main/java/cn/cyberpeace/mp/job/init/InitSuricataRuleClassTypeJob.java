package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.situationaware.service.SuricataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InitSuricataRuleClassTypeJob {

    @Autowired
    private SuricataService suricataService;

    @MpInitiate(description = "suricata规则ClassType字段生成", id = "init_suricata_rule_class_type_field")
    public void run() {
        TaskCommon.executeTask(() -> {
            log.info("do InitSuricataRuleClassTypeJob start");

            suricataService.scanRuleDataByPkAndInitClassType();

            log.info("do InitSuricataRuleClassTypeJob end");

        });

    }
}
