package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.common.constant.CommonConstant;
import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;
import cn.cyberpeace.mp.situationaware.service.SuricataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class InitFlowSuricataRuleFileJob {

    @Autowired
    private CollectionAppConfig collectionAppConfig;

    @Autowired
    private SuricataService suricataService;



    @MpInitiate(description = "流量采集suricata规则文件生成", id = "init_suricata_rule_file")
    public void run() {
        FileUtil.mkdir(collectionAppConfig.getSuricataRuleDir());
        TaskCommon.executeTask(() -> {
            log.info("syncDir do InitFlowSuricataRuleFileJob start");
            suricataService.convertAllRuleDataToRuleFile(StringUtils.joinWith(FileUtil.FILE_SEPARATOR, collectionAppConfig.getSuricataRuleDir(), "suricata_rule_from_mp.rules"));
            List<LocalFileMappingModel> mappings =
                    LocalFileMappingCommon.syncDir(CommonConstant.SURICATA_RULE_GROUP,
                            collectionAppConfig.getSuricataRuleDir());

            log.info("suricata file: {}", mappings);
            log.info("syncDir do InitFlowSuricataRuleFileJob end");
        });

    }


}
