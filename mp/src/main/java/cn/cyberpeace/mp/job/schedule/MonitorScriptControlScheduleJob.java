package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.devopscluster.service.MonitorScriptControlService;
import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.WazuhCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
@Slf4j
public class MonitorScriptControlScheduleJob {

    @Autowired
    private MonitorScriptControlService monitorScriptControlService;

    @Autowired
    private CollectionAppConfig collectionAppConfig;

    @MpScheduled(cron = "0 0/5 * * * ?", description = "检查控制节点和计算节点上是否需要安装或重装探针", id = "config_monitor_script_control")
    public void configMonitorScriptControl() {
        if (!collectionAppConfig.getBaseAppConfig().isRunInit()) {
            return;
        }
        log.info("start try to refresh controller and compute agent script");
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
        log.info("end try to refresh controller and compute agent script");
    }
}
