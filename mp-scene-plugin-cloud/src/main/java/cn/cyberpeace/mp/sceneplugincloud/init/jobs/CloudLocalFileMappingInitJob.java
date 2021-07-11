package cn.cyberpeace.mp.sceneplugincloud.init.jobs;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.ContainerFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class CloudLocalFileMappingInitJob extends AbstractInitJob {

    @Resource
    private BaseAppConfig baseAppConfig;

    @Autowired
    private CloudAppConfig cloudAppConfig;

    @Override
    protected boolean doExecute() {
        
        if (!baseAppConfig.isRunInit()) {
            return true;
        }
        LocalFileMappingCommon.syncDir(ContainerFileUtils.CONTAINER_HELP_INSTALLER_GROUP,
                cloudAppConfig.getContainerHelpInstallerDir());
        return true;
    }

    @Override
    protected boolean getSupportRefresh() {
        return false;
    }
}
