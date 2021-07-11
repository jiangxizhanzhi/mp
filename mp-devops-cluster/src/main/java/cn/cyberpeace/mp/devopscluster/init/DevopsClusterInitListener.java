package cn.cyberpeace.mp.devopscluster.init;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.devopscluster.service.MonitorScriptControlService;
import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.WazuhCommon;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;


@Order(100)
@Component
@Slf4j
public class DevopsClusterInitListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private MonitorScriptControlService monitorScriptControlService;

    @Autowired
    private CollectionAppConfig collectionAppConfig;


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        TaskCommon.executeTask(() -> {
            if (!collectionAppConfig.getBaseAppConfig().isRunInit()) {
                return;
            }
            
            String bizScriptInstallerDir = collectionAppConfig.getBizScriptsInstallerDir();
            if(!FileUtil.exist(bizScriptInstallerDir)){
                FileUtil.mkdir(collectionAppConfig.getBizScriptsInstallerDir());
            }
            List<LocalFileMappingModel> mappings =
                    LocalFileMappingCommon.syncDir(WazuhCommon.BIZ_SCRIPTS_GROUP,
                            collectionAppConfig.getBizScriptsInstallerDir());
            try {
                monitorScriptControlService.init(mappings);
            } catch (Exception e) {
                log.error("failed to init monitor script", e);
            }
        });
    }
}
