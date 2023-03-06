package cn.cyberpeace.mp.cscene.init;

import cn.cyberpeace.mp.cscene.init.jobs.SceneNodeNumInitJob;
import cn.cyberpeace.mp.sceneplugincloud.init.jobs.AbstractInitJob;
import cn.cyberpeace.mp.sceneplugincloud.init.jobs.CloudInstanceLinkInitJob;
import cn.cyberpeace.mp.sceneplugincloud.init.jobs.CloudLocalFileMappingInitJob;
import cn.cyberpeace.mp.sceneplugincloud.init.jobs.CloudMessageConvertInitJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 初始化任务
 * @Author maojun
 * @Date 2022/8/5
 */
@Slf4j
@Component
public class CronInit {

    /**
     * 初始化场景链路状态的线程池和更新周期
     */
    @Resource
    private CloudInstanceLinkInitJob cloudInstanceLinkInitJob;

    /**
     * 初始化云平台错误码转换的配置
     */
    @Resource
    private CloudMessageConvertInitJob cloudMessageConvertInitJob;

    /**
     * 云平台本地文件映射初始化任务
     */
    @Resource
    private CloudLocalFileMappingInitJob cloudLocalFileMappingInitJob;

    /**
     * 初始化场景使用的节点数量
     */
    @Resource
    private SceneNodeNumInitJob sceneNodeNumInitJob;

    @Scheduled(cron = "0 0/15 * * * ?")
    public void execute() {
        List<AbstractInitJob> jobs = new ArrayList<>();
        jobs.add(cloudInstanceLinkInitJob);
        jobs.add(cloudMessageConvertInitJob);
        jobs.add(cloudLocalFileMappingInitJob);
        jobs.add(sceneNodeNumInitJob);
        for (var job : jobs) {
            try {
                job.executed();
            } catch (Exception e) {
                log.error("failed to execute job : {}", job.getClass().getName(), e);
            }
        }
    }
}
