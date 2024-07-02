package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.ContainerFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName CloudLocalFileMappingInitJob
 * @Description
 * @Author maojun
 * @Date 2022/8/5
 * @Version 1.0.0
 */
@Component
public class CloudLocalFileMappingInitJob  {

    @Resource
    private BaseAppConfig baseAppConfig;

    @Autowired
    private CloudAppConfig cloudAppConfig;

    @MpInitiate(description = "初始化容器目录", id = "container_local_file_mapping_init")
    public void doExecute() {
        // 本地开发环境不运行资源的更新和删除
        if (!baseAppConfig.isRunInit()) {
            return;
        }
        LocalFileMappingCommon.syncDir(ContainerFileUtils.CONTAINER_HELP_INSTALLER_GROUP,
                cloudAppConfig.getContainerHelpInstallerDir());
    }
}
