package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.job.init.MpInitiate;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.scene.config.SceneAppConfig;
import cn.cyberpeace.mp.scene.util.InstallerUtils;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.ContainerFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SceneInit {

    @Resource
    private SceneAppConfig sceneAppConfig;

    @Resource
    private CloudAppConfig cloudAppConfig;

    @MpInitiate(description = "初始化各系统目录和文件", id = "init_installer_dir_and_content")
    public void execute() {
        // 本地开发环境不运行资源的更新和删除
        if (!sceneAppConfig.getBaseAppConfig().isRunInit()) {
            return;
        }
        FileUtil.mkdir(sceneAppConfig.getWindowsHelpInstallerDir());
        TaskCommon.executeTask(() -> {
            LocalFileMappingCommon.syncDir(InstallerUtils.WINDOWS_HELP_INSTALLER_GROUP,
                    sceneAppConfig.getWindowsHelpInstallerDir());
        });

        FileUtil.mkdir(cloudAppConfig.getContainerHelpInstallerDir());
        LocalFileMappingCommon.syncDir(ContainerFileUtils.CONTAINER_HELP_INSTALLER_GROUP,
                                       cloudAppConfig.getContainerHelpInstallerDir());
    }

}
