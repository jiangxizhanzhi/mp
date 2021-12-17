package cn.cyberpeace.mp.sceneplugincloud.init;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.ContainerFileUtils;
import cn.hutool.core.io.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ScenePluginCloudInit implements ApplicationRunner {

    @Autowired
    private BaseAppConfig baseAppConfig;

    @Autowired
    private CloudAppConfig cloudAppConfig;

    @Override
    public void run(ApplicationArguments args) {
        // 本地开发环境不运行资源的更新和删除
        if (!baseAppConfig.isRunInit()) {
            return;
        }
        FileUtil.mkdir(cloudAppConfig.getContainerHelpInstallerDir());
        TaskCommon.executeTask(() -> {
            LocalFileMappingCommon.syncDir(ContainerFileUtils.CONTAINER_HELP_INSTALLER_GROUP,
                    cloudAppConfig.getContainerHelpInstallerDir());
        });
    }

}
