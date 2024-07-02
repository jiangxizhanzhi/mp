package cn.cyberpeace.mp.cscene.report.rabbitmq.test_receiver;

import cn.cyberpeace.mp.base.message.FormatMessage;
import cn.cyberpeace.mp.base.message.SnakeJsonMessageConverter;
import cn.cyberpeace.mp.base.message.rabbitmq.BaseSimpleMessageReceiver;
import cn.cyberpeace.mp.cscene.report.rabbitmq.CsceneReportRabbitmqSender;
import cn.cyberpeace.mp.cscene.report.rabbitmq.RabbitmqCsceneReporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName CsceneReportRabbitmqReceiver
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSimpleMessageReceiver
 */
@Slf4j
//@Component  // 测试接收
public class CsceneReportRabbitmqReceiver extends BaseSimpleMessageReceiver {

    /**
     * CsceneReportRabbitmqReceiver
     *
     * @param messageConverter
     */
    @Autowired
    public CsceneReportRabbitmqReceiver(SnakeJsonMessageConverter messageConverter) {
        super(messageConverter);
    }

    /**
     * receive
     *
     * @param message
     */
    @RabbitListener(queues = CsceneReportRabbitmqSender.CSCENE_QUEUE_NAME)
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
        if (RabbitmqCsceneReporter.MESSAGE_TYPE_DELETE_CSCENE_INSTANCE.equals(messageType)) {
            FormatMessage<String> stringMessage = toFormatMessage(formatMessage);
            handleDeleteCsceneInstance(stringMessage.getData());
        } else {
            log.info("Receive unexcepted scene report message: type[{}] data[{}]", messageType, formatMessage.getData());
        }
    }

    /**
     * handleDeleteCsceneInstance
     *
     * @param csceneInstanceId
     */
    public void handleDeleteCsceneInstance(String csceneInstanceId) {
        log.info("Receive delete_cscene_instance report message: {}", csceneInstanceId);
    }

}
