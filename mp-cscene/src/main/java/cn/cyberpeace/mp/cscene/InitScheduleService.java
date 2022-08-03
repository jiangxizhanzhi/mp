package cn.cyberpeace.mp.cscene;

import cn.cyberpeace.mp.cscene.init.CsceneInit;
import cn.cyberpeace.mp.scene.init.SceneInit;
import cn.cyberpeace.mp.sceneplugincloud.init.ScenePluginCloudInit;
import cn.cyberpeace.mp.sceneplugincollection.init.ScenePluginCollectionInit;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

public class InitScheduleService {
    // 每5分钟执行一次
    public static final long REFRESH_RATE = 5;

    private static CsceneInit csceneInit = new CsceneInit();

    private static SceneInit sceneInit = new SceneInit();

    private static ScenePluginCloudInit scenePluginCloudInit = new ScenePluginCloudInit();

    private static ScenePluginCollectionInit scenePluginCollectionInit = new ScenePluginCollectionInit();

    @Scheduled(initialDelay = REFRESH_RATE, fixedRate = REFRESH_RATE, timeUnit = TimeUnit.MINUTES)
    public void initSchedule() {
        csceneInit.init();
        sceneInit.init();
        scenePluginCloudInit.init();
        scenePluginCollectionInit.init();
    }
}
