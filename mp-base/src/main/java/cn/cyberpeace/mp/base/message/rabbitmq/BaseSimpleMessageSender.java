package cn.cyberpeace.mp.base.message.rabbitmq;

import cn.cyberpeace.mp.base.message.FormatMessage;
import lombok.Data;
import org.springframework.amqp.core.AmqpTemplate;

import java.util.Map;

@Data
public abstract class BaseSimpleMessageSender {

    private AmqpTemplate amqpTemplate;

    private SimpleMessageConfig messageConfig;

    public void send(String message) {
        send(message, null);
    }

    public void send(String message, String messageType) {
        send(message, messageType, null);
    }

    public void send(String message, String messageType, Map<String, Object> context) {
        var formatMessage = FormatMessage.fromMessage(message);
        formatMessage.setType(messageType);
        if (context != null) {
            formatMessage.getContext().putAll(context);
        }
        amqpTemplate.convertAndSend(messageConfig.getExchangeName(), messageConfig.getRoutingKey(),
                formatMessage.toMessageString());
    }

}
