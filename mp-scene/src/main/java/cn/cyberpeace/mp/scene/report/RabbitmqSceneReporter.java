package cn.cyberpeace.mp.scene.report;

import cn.cyberpeace.mp.scene.core.bo.CollectedSceneBO;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneReporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitmqSceneReporter implements SceneReporter {
    @Override
    public void reportScene(CollectedSceneBO scene) {
        log.info("Rabbitmq report scene: {}", scene);
    }

    @Override
    public void reportElement(CollectedSceneElementBO sceneElement) {
        log.info("Rabbitmq report scene element: {}", sceneElement);
    }
}
