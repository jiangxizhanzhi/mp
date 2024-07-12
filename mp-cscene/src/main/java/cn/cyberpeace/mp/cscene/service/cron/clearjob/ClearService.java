 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ClearService.java.
 // File size 1592.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.service.cron.clearjob;

import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.scene.repository.SceneElementPortMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementPortMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ClearService
 * @Description
 * @Author maojun
 * @Date 2022/7/15
 * @Version 1.0.0
 */
@Slf4j
@Service
public class ClearService {
    @Resource
    private SceneElementPortMapper sceneElementPortMapper;

    @Resource
    private SceneElementMapper sceneElementMapper;

    @Resource
    private CloudPluginSceneElementPortMapper portMapper;

    /**
     * clearUselessResource
     */
    public void clearUselessResource() {
        List<AbstractClearJob> jobs = new ArrayList<>() {{
            this.add(new NpcClearJob(sceneElementPortMapper, portMapper, sceneElementMapper));
            this.add(new ContainerClearJob(sceneElementMapper));
            this.add(new VmClearJob(sceneElementMapper));
            this.add(new VrClearJob(sceneElementMapper));
            this.add(new NetClearJob(sceneElementMapper));
        }};
        for (AbstractClearJob job : jobs) {
            try {
                job.action();
            } catch (Exception e) {
                log.error("faild to execute clear job {} ", job.getClass().getName(), e);
            }
        }
    }
}
