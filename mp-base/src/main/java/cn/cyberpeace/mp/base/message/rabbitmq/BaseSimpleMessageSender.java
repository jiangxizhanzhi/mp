package cn.cyberpeace.mp.base.message.rabbitmq;

import cn.cyberpeace.mp.base.message.FormatMessage;
import cn.cyberpeace.mp.base.message.MessageConverter;
import lombok.Data;
import org.springframework.amqp.core.AmqpTemplate;

import java.util.Map;

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
        var formatMessage = new FormatMessage<T>(data, type, context);
        amqpTemplate.convertAndSend(messageConfig.getExchangeName(), messageConfig.getRoutingKey(),
                messageConverter.toMessageString(formatMessage));
    }

}
