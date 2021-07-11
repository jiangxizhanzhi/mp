package cn.cyberpeace.mp.scene.init;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.scene.config.SceneAppConfig;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
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
        
        
        var sceneHandler = new SceneHandler();
        ClassUtils.loadClassFields(sceneHandler);
        BeanCommon.loadBeanFields(sceneHandler);
        
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
