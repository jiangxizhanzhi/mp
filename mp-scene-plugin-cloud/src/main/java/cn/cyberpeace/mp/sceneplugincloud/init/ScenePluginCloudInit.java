package cn.cyberpeace.mp.sceneplugincloud.init;

import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import cn.cyberpeace.mp.sceneplugincloud.init.jobs.AbstractInitJob;
import cn.cyberpeace.mp.sceneplugincloud.init.jobs.CloudLocalFileMappingInitJob;
import cn.cyberpeace.mp.sceneplugincloud.init.jobs.CloudMessageConvertInitJob;
import cn.cyberpeace.mp.sceneplugincloud.init.jobs.CloudInstanceLinkInitJob;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ScenePluginCloudInit implements ApplicationRunner {

    @Resource
    private CloudAppConfig cloudAppConfig;

    @Resource
    private CloudLocalFileMappingInitJob cloudLocalFileMappingInitJob;

    @Resource
    private CloudMessageConvertInitJob cloudMessageConvertInitJob;

    @Resource
    private CloudInstanceLinkInitJob cloudInstanceLinkInitJob;

    @Override
    public void run(ApplicationArguments args) {

        cloudAppConfig.init();

        
        List<AbstractInitJob> syncJobs = new ArrayList<>();
        syncJobs.add(cloudMessageConvertInitJob);
        syncJobs.add(cloudInstanceLinkInitJob);
        for (var job : syncJobs) {
            try {
                job.executed();
            } catch (Exception e) {
                log.error("failed to execute job : {}", job.getClass().getName(), e);
            }
        }

        
        FileUtil.mkdir(cloudAppConfig.getContainerHelpInstallerDir());
        TaskCommon.executeTask(() -> {
            cloudLocalFileMappingInitJob.executed();
        });
    }

}
