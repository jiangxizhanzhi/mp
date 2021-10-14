package cn.cyberpeace.mp.base.message.rabbitmq;


import cn.cyberpeace.mp.base.message.MessageConverter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;

import java.nio.charset.StandardCharsets;

@Slf4j
@Data
public abstract class BaseMessageReceiver {

    private MessageConverter messageConverter;

    private String getMessageBody(Message message) {
        var body = message.getBody();
        if (body == null) {
            return null;
        }
        return new String(body, StandardCharsets.UTF_8);
    }

    public abstract void receive(Message message);

    public <T> T parseMessage(Message message, Class<T> resultClass) {
        var body = getMessageBody(message);
        if (StringUtils.isEmpty(body)) {
            log.error("Message receive with empty body: {}", message);
            return null;
        }

        T formatMessage;
        try {
            formatMessage = messageConverter.fromMessageString(body, resultClass);
        } catch (Exception e) {
            log.error("Message[{}] parse format message error: {}", message, e);
            return null;
        }

        return formatMessage;
    }

}
