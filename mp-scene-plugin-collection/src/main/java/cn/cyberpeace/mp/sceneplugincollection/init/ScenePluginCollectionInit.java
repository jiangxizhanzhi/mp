package cn.cyberpeace.mp.sceneplugincollection.init;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.WazuhCommon;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScenePluginCollectionInit implements ApplicationRunner {

    @Autowired
    private CollectionAppConfig collectionAppConfig;

    @Override
    public void run(ApplicationArguments args) {
        FileUtil.mkdir(collectionAppConfig.getWazuhAgentInstallerDir());
        TaskCommon.executeTask(() -> {
            log.info("syncDir do ScenePluginCollectionInit start");
            LocalFileMappingCommon.syncDir(WazuhCommon.WAZUH_AGENT_GROUP,
                    collectionAppConfig.getWazuhAgentInstallerDir());
            log.info("syncDir do ScenePluginCollectionInit end");
        });
    }

}
