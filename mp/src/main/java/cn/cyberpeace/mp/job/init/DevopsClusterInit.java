package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.job.init.MpInitiate;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.devopscluster.service.MonitorScriptControlService;
import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.WazuhCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/8/9 09:22
 */
@Component
@Slf4j
public class DevopsClusterInit {

    @Autowired
    private MonitorScriptControlService monitorScriptControlService;

    @Autowired
    private CollectionAppConfig collectionAppConfig;


    @MpInitiate(description = "初始化时检查控制节点和计算节点上是否需要安装", id = "init_monitor_script")
    public void action() {

        TaskCommon.executeTask(() -> {
            if (!collectionAppConfig.getBaseAppConfig().isRunInit()) {
                return;
            }
            //Add by wangssh 230531
            String bizScriptInstallerDir = collectionAppConfig.getBizScriptsInstallerDir();
            File directory = new File(bizScriptInstallerDir);
            if(!directory.exists()){
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
        });
    }
}
