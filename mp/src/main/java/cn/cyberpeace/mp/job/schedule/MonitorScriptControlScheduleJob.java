package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.devopscluster.model.CpNodeMonitorScriptConfigModel;
import cn.cyberpeace.mp.devopscluster.repository.CpNodeMonitorScriptConfigMapper;
import cn.cyberpeace.mp.devopscluster.request.CloudPlatformPageRequest;
import cn.cyberpeace.mp.devopscluster.service.ServiceMonitorService;
import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.devopscluster.service.MonitorScriptControlService;
import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.WazuhCommon;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

@Component
@Slf4j
public class MonitorScriptControlScheduleJob {

    @Autowired
    private MonitorScriptControlService monitorScriptControlService;

    @Resource
    private ServiceMonitorService serviceMonitorService ;

    @Autowired
    private CollectionAppConfig collectionAppConfig;

    @Resource
    private CpNodeMonitorScriptConfigMapper scriptConfigMapper ;

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
        this.checkInstallRecord();
        try {
            monitorScriptControlService.init(mappings);
        } catch (Exception e) {
            log.error("failed to init monitor script", e);
        }
        log.info("end try to refresh controller and compute agent script");
    }

    private void checkInstallRecord() {
        try {
            var list = serviceMonitorService.getActiveHostNameList(null);
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            for (String hostName : list) {
                CloudPlatformPageRequest request = new CloudPlatformPageRequest();
                request.setHostName(hostName);
                // 如果没有查到监控数据 需要重新下发下监控脚本
                var result = serviceMonitorService.cpPage(request);
                if (result == null || CollectionUtils.isEmpty(result.getObjs())) {
                    scriptConfigMapper.filterDelete(new HumanCondition().filterEqual(CpNodeMonitorScriptConfigModel::getHostname, hostName));
                }
            }
        } catch (Exception e) {
            log.error("failed to check installed script ");
        }
    }
}
