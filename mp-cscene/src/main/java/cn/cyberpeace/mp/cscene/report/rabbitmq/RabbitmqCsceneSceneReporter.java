package cn.cyberpeace.mp.cscene.report.rabbitmq;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.cscene.context.CsceneContextHolder;
import cn.cyberpeace.mp.cscene.report.bo.CollectedCsceneBO;
import cn.cyberpeace.mp.cscene.report.bo.CollectedCsceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneBO;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneReporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitmqCsceneSceneReporter implements SceneReporter {

    public static final String MESSAGE_TYPE_SCENE = "scene";

    public static final String MESSAGE_TYPE_SCENE_ELEMENT = "scene_element";

    @Autowired
    private CsceneSceneReportRabbitmqSender messageSender;

    @Override
    public void reportScene(CollectedSceneBO scene) {
        var cscene = CollectedCsceneBO.fromCollectedSceneBO(scene);
        cscene.setCsceneId(CsceneContextHolder.getCsceneInstanceId());
        cscene.setCallAction(CsceneContextHolder.getCallAction());
        log.info("scene message, type :{}, content :{}", MESSAGE_TYPE_SCENE, JsonCommon.stringify(cscene));
        messageSender.send(cscene, MESSAGE_TYPE_SCENE);
    }

    @Override
    public void reportElement(CollectedSceneElementBO sceneElement) {
        var csceneElement = CollectedCsceneElementBO.fromCollectedSceneElementBO(sceneElement);
        csceneElement.setCsceneId(CsceneContextHolder.getCsceneInstanceId());
        csceneElement.setCallAction(CsceneContextHolder.getCallAction());
        log.info("scene message, type :{}, content :{}", MESSAGE_TYPE_SCENE_ELEMENT, JsonCommon.stringify(csceneElement));
        messageSender.send(csceneElement, MESSAGE_TYPE_SCENE_ELEMENT);
    }
}
