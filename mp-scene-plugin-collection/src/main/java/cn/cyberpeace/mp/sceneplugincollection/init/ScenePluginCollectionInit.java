package cn.cyberpeace.mp.sceneplugincollection.init;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;
import cn.cyberpeace.mp.sceneplugincollection.config.WazuhLogProbeLocationConfig;
import cn.cyberpeace.mp.sceneplugincollection.config.WazuhModifyEventConfig;
import cn.cyberpeace.mp.sceneplugincollection.config.WazuhProbeConfig;
import cn.cyberpeace.mp.sceneplugincollection.event.LocalFileSyncEventPublisher;
import cn.cyberpeace.mp.sceneplugincollection.event.LocalFileSyncSuccessEvent;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.WazuhCommon;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ScenePluginCollectionInit implements ApplicationRunner {

    @Autowired
    private CollectionAppConfig collectionAppConfig;

    @Autowired
    private LocalFileSyncEventPublisher eventPublisher;

    @Override
    public void run(ApplicationArguments args) {
        
        WazuhProbeConfig.init();
        WazuhLogProbeLocationConfig.init();
        WazuhModifyEventConfig.init();
        
        if (!collectionAppConfig.getBaseAppConfig().isRunInit()) {
            return;
        }
        FileUtil.mkdir(collectionAppConfig.getWazuhAgentInstallerDir());
        TaskCommon.executeTask(() -> {
            log.info("syncDir do ScenePluginCollectionInit start");
            List<LocalFileMappingModel> mappings =
                    LocalFileMappingCommon.syncDir(WazuhCommon.WAZUH_AGENT_GROUP,
                    collectionAppConfig.getWazuhAgentInstallerDir());
            eventPublisher.publishEvent(new LocalFileSyncSuccessEvent(mappings));
            log.info("syncDir do ScenePluginCollectionInit end");
        });
    }

}
