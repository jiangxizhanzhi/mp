package cn.cyberpeace.mp.cscene.report;

import cn.cyberpeace.mp.base.message.FormatMessage;
import cn.cyberpeace.mp.base.message.rabbitmq.BaseSimpleMessageReceiver;
import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneBO;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneElementBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
//@Component  // 测试接收
public class SceneReportMessageReceiver extends BaseSimpleMessageReceiver {

    @RabbitListener(queues = SceneReportMessageSender.CSCENE_QUEUE_NAME)
    @Override
    public void receive(Message message) {
        super.receive(message);
    }

    @Override
    public void handleMessage(FormatMessage formatMessage) {
        String messageType = formatMessage.getType();
        String content = formatMessage.getContent();
        if (RabbitmqSceneReporter.MESSAGE_TYPE_SCENE.equals(messageType)) {
            handleSceneContent(content);
        } else if (RabbitmqSceneReporter.MESSAGE_TYPE_SCENE_ELEMENT.equals(messageType)) {
            handleSceneElementContent(content);
        } else {
            log.info("Receive unexcepted scene report message: type[{}] content[{}]", messageType, content);
        }
    }

    public void handleSceneContent(String content) {
        try {
            log.info("Receive scene report message: {}", JsonUtils.toSnakeObject(content, CollectedSceneBO.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleSceneElementContent(String content) {
        try {
            log.info("Receive scene element report message: {}", JsonUtils.toSnakeObject(content,
                    CollectedSceneElementBO.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
