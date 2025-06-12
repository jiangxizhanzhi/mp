package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;
import cn.cyberpeace.mp.sceneplugincollection.config.WazuhLogProbeLocationConfig;
import cn.cyberpeace.mp.sceneplugincollection.config.WazuhModifyEventConfig;
import cn.cyberpeace.mp.sceneplugincollection.config.WazuhProbeConfig;
import cn.cyberpeace.mp.sceneplugincollection.event.LocalFileSyncEventPublisher;
import cn.cyberpeace.mp.sceneplugincollection.event.LocalFileSyncSuccessEvent;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.WazuhCommon;
import cn.cyberpeace.mp.sceneplugincollection.util.InitCustomProbe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ScenePluginCollectionInitJob {

    @Autowired
    private CollectionAppConfig collectionAppConfig;

    @Autowired
    private LocalFileSyncEventPublisher eventPublisher;

    @MpInitiate(description = "初始化探针配置", id = "wazuh_agent_installer_init")
    public void execute() {
        // 初始化探针配置
        WazuhProbeConfig.init();
        WazuhLogProbeLocationConfig.init();
        WazuhModifyEventConfig.init();
        // 本地开发环境不运行资源的更新和删除
        if (!collectionAppConfig.getBaseAppConfig().isRunInit()) {
            return;
        }
        FileUtil.mkdir(collectionAppConfig.getWazuhAgentInstallerDir());
        TaskCommon.executeTask(() -> {
            log.info("syncDir init_custom start");
            // 将自定义探针示例 同步到本地目录中去
            InitCustomProbe.init();
            LocalFileMappingCommon.syncDir("init_custom", collectionAppConfig.getInitCustomProbeDir());
            log.info("syncDir init_custom end");

            log.info("syncDir do ScenePluginCollectionInit start");
            List<LocalFileMappingModel> mappings =
                    LocalFileMappingCommon.syncDir(WazuhCommon.WAZUH_AGENT_GROUP,
                    collectionAppConfig.getWazuhAgentInstallerDir());
            eventPublisher.publishEvent(new LocalFileSyncSuccessEvent(mappings));
            log.info("syncDir do ScenePluginCollectionInit end");
        });
    }

}
