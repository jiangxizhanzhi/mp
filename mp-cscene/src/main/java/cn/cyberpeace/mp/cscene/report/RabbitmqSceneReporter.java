package cn.cyberpeace.mp.cscene.report;

import cn.cyberpeace.mp.base.message.rabbitmq.BaseSimpleMessageSender;
import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneBO;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneReporter;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitmqSceneReporter implements SceneReporter {

    public static final String MESSAGE_TYPE_SCENE = "scene";

    public static final String MESSAGE_TYPE_SCENE_ELEMENT = "scene_element";

    @Autowired
    private BaseSimpleMessageSender messageSender;

    @Override
    public void reportScene(CollectedSceneBO scene) {
        messageSender.send(scene, MESSAGE_TYPE_SCENE);
    }

    @Override
    public void reportElement(CollectedSceneElementBO sceneElement) {
        messageSender.send(sceneElement, MESSAGE_TYPE_SCENE_ELEMENT);
    }
}
