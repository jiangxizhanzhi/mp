package cn.cyberpeace.mp.cscene.report.rabbitmq;

import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.context.CsceneContextHolder;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneStatusBO;
import cn.cyberpeace.mp.scene.core.scene.StatusReporter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description
 * @Author maojun
 * @Date 2022/3/21
 */
@Component("sceneStatusReporter")
public class SceneStatusReporter implements StatusReporter<CollectedSceneStatusBO> {
    public static final String MESSAGE_TYPE_SCENE = "scene_status_change";

    @Resource
    private StatusChangeRabbitmqSender messageSender;

    @Resource
    private CsceneAppConfig csceneAppConfig;

    @Override
    public void reportStatus(CollectedSceneStatusBO sceneStatus) {
        sceneStatus.setCsceneId(CsceneContextHolder.getCsceneInstanceId());
        messageSender.send(sceneStatus, MESSAGE_TYPE_SCENE, null, csceneAppConfig.getSceneMessageTimeout());
    }
}
