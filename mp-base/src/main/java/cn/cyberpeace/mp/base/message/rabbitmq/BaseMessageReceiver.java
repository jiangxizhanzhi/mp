package cn.cyberpeace.mp.base.message.rabbitmq;


import cn.cyberpeace.mp.base.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

import java.nio.charset.StandardCharsets;

@Slf4j
public abstract class BaseMessageReceiver<T> {

    public abstract void receive(Message message);

    public T parseMessage(Message message, Class<T> resultClass) {
        var body = message.getBody();
        if (body == null || body.length == 0) {
            log.error("Message receive with empty body: {}", message);
            return null;
        }

        T formatMessage;
        try {
            formatMessage = JsonUtils.parse(new String(body, StandardCharsets.UTF_8), resultClass);
        } catch (Exception e) {
            log.error("Message[{}] parse format message error: {}", message, e);
            return null;
        }

        return formatMessage;
    }

    public abstract void handleMessage(T formatMessage);

}
