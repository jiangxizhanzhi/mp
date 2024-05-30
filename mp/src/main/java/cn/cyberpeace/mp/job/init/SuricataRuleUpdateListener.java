package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;
import cn.cyberpeace.mp.situationaware.event.SuricataRuleUpdateEvent;
import cn.cyberpeace.mp.situationaware.service.SuricataService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SuricataRuleUpdateListener implements ApplicationListener<SuricataRuleUpdateEvent> {
    private boolean genFileTaskRunning = false;
    private LocalDateTime latestStartGenFileTime = LocalDateTime.now();

    @Autowired
    private SuricataService suricataService;
    @Autowired
    private CollectionAppConfig collectionAppConfig;
    @Override
    public void onApplicationEvent(SuricataRuleUpdateEvent event) {
        LocalDateTime latestReceiveEventTime = latestStartGenFileTime;
        synchronized (SuricataRuleUpdateListener.class) {
            latestReceiveEventTime = LocalDateTime.now();

            if (genFileTaskRunning) {
                return;
            } else {
                genFileTaskRunning = true;
                latestStartGenFileTime = latestReceiveEventTime;
            }
        }

        suricataService.convertAllRuleDataToRuleFile(StringUtils.joinWith(FileUtil.FILE_SEPARATOR, collectionAppConfig.getSuricataRuleDir(), "suricata_rule_from_mp.rules"));
        //说明运行过程中还有生成修改规则文件
        if (!latestStartGenFileTime.equals(latestReceiveEventTime)) {
            suricataService.convertAllRuleDataToRuleFile(StringUtils.joinWith(FileUtil.FILE_SEPARATOR, collectionAppConfig.getSuricataRuleDir(), "suricata_rule_from_mp.rules"));
            latestStartGenFileTime = latestReceiveEventTime;
        }
    }
}
