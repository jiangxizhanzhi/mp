package cn.cyberpeace.mp.devops.cluster.init;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.devops.cluster.service.MonitorScriptControlService;
import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.WazuhCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/8/9 09:22
 */
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
            List<LocalFileMappingModel> mappings =
                    LocalFileMappingCommon.syncDir(WazuhCommon.BIZ_SCRIPTS_GROUP,
                            collectionAppConfig.getBizScriptsInstallerDir());
            monitorScriptControlService.init(mappings);
        });
    }
}
