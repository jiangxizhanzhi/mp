package cn.cyberpeace.mp.devopscluster.task;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.devopscluster.service.MonitorScriptControlService;
import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.WazuhCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
@Slf4j
public class MonitorScriptControlTask {

    @Autowired
    private MonitorScriptControlService monitorScriptControlService;

    @Autowired
    private CollectionAppConfig collectionAppConfig;

    @Scheduled(cron = "0 0/5 * * * ?")
    private void configMonitorScriptControl() {
        if (!collectionAppConfig.getBaseAppConfig().isRunInit()) {
            return;
        }
        //Add by wangssh 230531
        String bizScriptInstallerDir = collectionAppConfig.getBizScriptsInstallerDir();
        File directory = new File(bizScriptInstallerDir);
        if (!directory.exists()) {
            directory.mkdir();
        }
        List<LocalFileMappingModel> mappings =
                LocalFileMappingCommon.syncDir(WazuhCommon.BIZ_SCRIPTS_GROUP,
                        collectionAppConfig.getBizScriptsInstallerDir());
        try {
            monitorScriptControlService.init(mappings);
        } catch (Exception e) {
            log.error("failed to init monitor script", e);
        }
    }
}
