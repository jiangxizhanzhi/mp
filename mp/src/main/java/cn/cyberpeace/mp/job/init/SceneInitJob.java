 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneInitJob.java.
 // File size 1705.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.scene.config.SceneAppConfig;
import cn.cyberpeace.mp.scene.util.InstallerUtils;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.ContainerFileUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName SceneInitJob
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
public class SceneInitJob {

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
