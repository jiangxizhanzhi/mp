package cn.cyberpeace.mp.cscene.init;

import cn.cyberpeace.mp.cscene.init.jobs.SceneNodeNumInitJob;
import cn.cyberpeace.mp.cscene.init.jobs.SceneUpdateStatusJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description
 * @Author maojun
 * @Date 2022/2/11
 */
@Slf4j
@Component
public class CsceneInit implements ApplicationRunner {

    @Resource
    private SceneNodeNumInitJob sceneNodeNumInitJob;
    @Resource
    private SceneUpdateStatusJob sceneUpdateStatusJob;

    @Override
    public void run(ApplicationArguments args) {
        try {
            sceneNodeNumInitJob.executed();
            sceneUpdateStatusJob.executed();
        } catch (Exception e) {
            log.error("failed to execute job : {}", sceneNodeNumInitJob.getClass().getName(), e);
        }
    }
}
