package cn.cyberpeace.mp.cscene.report.rabbitmq.test_receiver;

import cn.cyberpeace.mp.base.message.FormatMessage;
import cn.cyberpeace.mp.base.message.SnakeJsonMessageConverter;
import cn.cyberpeace.mp.base.message.rabbitmq.BaseSimpleMessageReceiver;
import cn.cyberpeace.mp.cscene.report.rabbitmq.CsceneSceneReportRabbitmqSender;
import cn.cyberpeace.mp.cscene.report.rabbitmq.RabbitmqCsceneSceneReporter;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneBO;
import cn.cyberpeace.mp.scene.core.bo.CollectedSceneElementBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName CsceneSceneReportRabbitmqReceiver
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSimpleMessageReceiver
 */
@Slf4j
//@Component  // 测试接收
public class CsceneSceneReportRabbitmqReceiver extends BaseSimpleMessageReceiver {

    /**
     * CsceneSceneReportRabbitmqReceiver
     *
     * @param messageConverter
     */
    @Autowired
    public CsceneSceneReportRabbitmqReceiver(SnakeJsonMessageConverter messageConverter) {
        super(messageConverter);
    }

    /**
     * receive
     *
     * @param message
     */
    @RabbitListener(queues = CsceneSceneReportRabbitmqSender.CSCENE_SCENE_REPORT_QUEUE_NAME)
    @Override
    public void receive(Message message) {
        super.receive(message);
    }

    /**
     * handleMessage
     *
     * @param formatMessage
     */
    @Override
    public void handleMessage(FormatMessage<?> formatMessage) {
        String messageType = formatMessage.getType();
        if (RabbitmqCsceneSceneReporter.MESSAGE_TYPE_SCENE.equals(messageType)) {
            handleSceneContent(toFormatMessage(formatMessage, CollectedSceneBO.class).getData());
        } else if (RabbitmqCsceneSceneReporter.MESSAGE_TYPE_SCENE_ELEMENT.equals(messageType)) {
            handleSceneElementContent(toFormatMessage(formatMessage, CollectedSceneElementBO.class).getData());
        } else {
            log.info("Receive unexcepted scene report message: type[{}] data[{}]", messageType, formatMessage.getData());
        }
    }

    /**
     * handleSceneContent
     *
     * @param scene
     */
    public void handleSceneContent(CollectedSceneBO scene) {
        log.info("Receive scene report message: {}", scene);
    }

    /**
     * handleSceneElementContent
     *
     * @param sceneElement
     */
    public void handleSceneElementContent(CollectedSceneElementBO sceneElement) {
        log.info("Receive scene element report message: {}", sceneElement);
    }

}
