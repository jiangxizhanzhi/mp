package cn.cyberpeace.mp.devops.cluster.init;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.InitRegisterService;
import cn.cyberpeace.mp.devops.cluster.config.IpAddressConfig;
import cn.cyberpeace.mp.devops.cluster.service.MonitorScriptControlService;
import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.WazuhCommon;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ServerConfigListenerRunner implements ApplicationRunner {

    @Autowired
    private InitRegisterService initRegisterService;

    @Autowired
    private MonitorScriptControlService monitorScriptControlService;

    @Autowired
    private IpAddressConfig ipAddressConfig;

    @Autowired
    private CollectionAppConfig collectionAppConfig;

    public String getUrl() {
        return "http://" + ipAddressConfig.getMp();
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            initRegisterService.register(getUrl());
            initRegisterService.initTarget();
            //

            TaskCommon.executeTask(() -> {
                if (!collectionAppConfig.getBaseAppConfig().isRunInit()) {
                    return;
                }
                log.info("syncDir do ScenePluginCollectionInit start");
                List<LocalFileMappingModel> mappings =
                        LocalFileMappingCommon.syncDir(WazuhCommon.BIZ_SCRIPTS_GROUP,
                                collectionAppConfig.getBizScriptsInstallerDir());
                monitorScriptControlService.init(mappings);
                log.info("syncDir do ScenePluginCollectionInit end");
            });

        } catch (Exception e) {
            log.error("ServerConfigListener onApplicationEvent error : {} ", ExceptionUtils.getStackTrace(e));
        }
    }
}
