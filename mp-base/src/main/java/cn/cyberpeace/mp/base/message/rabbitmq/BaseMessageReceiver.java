 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseMessageReceiver.java.
 // File size 1682.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.message.rabbitmq;


import cn.cyberpeace.mp.base.message.MessageConverter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName BaseMessageReceiver
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Data
public abstract class BaseMessageReceiver {

    private MessageConverter messageConverter;

    /**
     * getMessageBody
     *
     * @param message
     * @return
     */
    private String getMessageBody(Message message) {
        byte[] body = message.getBody();
        if (body == null) {
            return null;
        }
        return new String(body, StandardCharsets.UTF_8);
    }

    /**
     * receive
     *
     * @param message
     */
    public abstract void receive(Message message);

    /**
     * parseMessage
     *
     * @param message
     * @param resultClass
     * @return
     */
    public <T> T parseMessage(Message message, Class<T> resultClass) {
        String body = getMessageBody(message);
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
