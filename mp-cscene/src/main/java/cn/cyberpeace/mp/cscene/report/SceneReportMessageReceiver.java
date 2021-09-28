package cn.cyberpeace.mp.cscene.report;

import cn.cyberpeace.mp.base.message.FormatMessage;
import cn.cyberpeace.mp.base.message.SnakeJsonMessageConverter;
import cn.cyberpeace.mp.base.message.rabbitmq.BaseSimpleMessageReceiver;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneBO;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneElementBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
//@Component  // 测试接收
public class SceneReportMessageReceiver extends BaseSimpleMessageReceiver {

    @Autowired
    public SceneReportMessageReceiver(SnakeJsonMessageConverter messageConverter) {
        super(messageConverter);
    }

    @RabbitListener(queues = SceneReportMessageSender.CSCENE_QUEUE_NAME)
    @Override
    public void receive(Message message) {
        super.receive(message);
    }

    @Override
    public void handleMessage(FormatMessage<Map<String, Object>> formatMessage) {
        String messageType = formatMessage.getType();
        if (RabbitmqSceneReporter.MESSAGE_TYPE_SCENE.equals(messageType)) {
            handleSceneContent(toFormatMessage(formatMessage, CollectedSceneBO.class).getData());
        } else if (RabbitmqSceneReporter.MESSAGE_TYPE_SCENE_ELEMENT.equals(messageType)) {
            handleSceneElementContent(toFormatMessage(formatMessage, CollectedSceneElementBO.class).getData());
        } else {
            log.info("Receive unexcepted scene report message: type[{}] data[{}]", messageType, formatMessage.getData());
        }
    }

    public void handleSceneContent(CollectedSceneBO scene) {
        log.info("Receive scene report message: {}", scene);
    }

    public void handleSceneElementContent(CollectedSceneElementBO sceneElement) {
        log.info("Receive scene element report message: {}", sceneElement);
    }

}
