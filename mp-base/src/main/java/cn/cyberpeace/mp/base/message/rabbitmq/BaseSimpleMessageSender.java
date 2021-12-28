package cn.cyberpeace.mp.base.message.rabbitmq;

import cn.cyberpeace.mp.base.message.FormatMessage;
import cn.cyberpeace.mp.base.message.MessageConverter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;

import java.util.Map;

@Slf4j
@Data
public abstract class BaseSimpleMessageSender {

    private AmqpTemplate amqpTemplate;

    private MessageConverter messageConverter;

    private SimpleMessageConfig messageConfig;

    protected BaseSimpleMessageSender(AmqpTemplate amqpTemplate, MessageConverter messageConverter,
                                      SimpleMessageConfig messageConfig) {
        setAmqpTemplate(amqpTemplate);
        setMessageConverter(messageConverter);
        setMessageConfig(messageConfig);
    }

    public <T> void send(T data) {
        send(data, null);
    }

    public <T> void send(T data, String type) {
        send(data, type, null);
    }

    public <T> void send(T data, String type, Map<String, Object> context) {
        FormatMessage<T> formatMessage = new FormatMessage<>(data, type, context);
        // todo 临时打印一下
        log.info("mq message:" + messageConverter.toMessageString(formatMessage));
        amqpTemplate.convertAndSend(messageConfig.getExchangeName(), messageConfig.getRoutingKey(),
                messageConverter.toMessageString(formatMessage));
    }

}
