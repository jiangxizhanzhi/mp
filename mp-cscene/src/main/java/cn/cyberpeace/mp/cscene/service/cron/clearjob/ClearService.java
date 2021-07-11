package cn.cyberpeace.mp.cscene.service.cron.clearjob;

import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.scene.repository.SceneElementPortMapper;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneElementPortMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class ClearService {
    @Resource
    private SceneElementPortMapper sceneElementPortMapper;

    @Resource
    private SceneElementMapper sceneElementMapper;

    @Resource
    private CloudPluginSceneElementPortMapper portMapper;

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
