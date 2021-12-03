package cn.cyberpeace.mp.scene.init;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.scene.config.SceneAppConfig;
import cn.cyberpeace.mp.scene.util.InstallerUtils;
import cn.hutool.core.io.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SceneInit implements ApplicationRunner {

    @Autowired
    private SceneAppConfig sceneAppConfig;

    @Override
    public void run(ApplicationArguments args) {
        // 本地开发环境不运行资源的更新和删除
        if (!sceneAppConfig.getBaseAppConfig().isRunInit()) {
            return;
        }
        FileUtil.mkdir(sceneAppConfig.getWindowsHelpInstallerDir());
        TaskCommon.executeTask(() -> {
            LocalFileMappingCommon.syncDir(InstallerUtils.WINDOWS_HELP_INSTALLER_GROUP,
                    sceneAppConfig.getWindowsHelpInstallerDir());
        });
    }

}
