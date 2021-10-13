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
import org.springframework.stereotype.Component;

@Slf4j
//@Component  // 测试接收
public class CsceneReportRabbitmqReceiver extends BaseSimpleMessageReceiver {

    @Autowired
    public CsceneReportRabbitmqReceiver(SnakeJsonMessageConverter messageConverter) {
        super(messageConverter);
    }

    @RabbitListener(queues = CsceneReportRabbitmqSender.CSCENE_QUEUE_NAME)
    @Override
    public void receive(Message message) {
        super.receive(message);
    }

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

    public void handleDeleteCsceneInstance(String csceneInstanceId) {
        log.info("Receive delete_cscene_instance report message: {}", csceneInstanceId);
    }

}
