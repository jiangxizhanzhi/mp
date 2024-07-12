 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseSimpleMessageSender.java.
 // File size 3186.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.message.rabbitmq;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.message.FormatMessage;
import cn.cyberpeace.mp.base.message.MessageConverter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

import java.util.Map;

/**
 * @ClassName BaseSimpleMessageSender
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Data
public abstract class BaseSimpleMessageSender {

    private AmqpTemplate amqpTemplate;

    private MessageConverter messageConverter;

    private SimpleMessageConfig messageConfig;

    /**
     * BaseSimpleMessageSender
     *
     * @param amqpTemplate
     * @param messageConverter
     * @param messageConfig
     */
    protected BaseSimpleMessageSender(AmqpTemplate amqpTemplate, MessageConverter messageConverter,
                                      SimpleMessageConfig messageConfig) {
        setAmqpTemplate(amqpTemplate);
        setMessageConverter(messageConverter);
        setMessageConfig(messageConfig);
    }

    /**
     * send
     *
     * @param data
     */
    public <T> void send(T data) {
        send(data, null);
    }

    /**
     * send
     *
     * @param data
     * @param type
     */
    public <T> void send(T data, String type) {
        send(data, type, null);
    }

    /**
     * send
     *
     * @param data
     * @param type
     * @param context
     */
    public <T> void send(T data, String type, Map<String, Object> context) {
        send(data, type, context, null);
    }

    /**
     * send
     *
     * @param data
     * @param type
     * @param context
     * @param timeout
     */
    public <T> void send(T data, String type, Map<String, Object> context, Long timeout) {
        FormatMessage<T> formatMessage = new FormatMessage<>(data, type, context);
        log.debug("mq content:{}", JsonCommon.stringify(formatMessage));

        BaseSimpleMessagePostProcessor messagePostProcessor = null;
        if (timeout != null) {
            messagePostProcessor = new BaseSimpleMessagePostProcessor();
            messagePostProcessor.setTimeout(timeout);
        }
        amqpTemplate.convertAndSend(messageConfig.getExchangeName(), messageConfig.getRoutingKey(),
                messageConverter.toMessageString(formatMessage), messagePostProcessor);
    }

    @Data
    public static class BaseSimpleMessagePostProcessor implements MessagePostProcessor {

        private Long timeout;

        /**
         * postProcessMessage
         *
         * @param message
         * @return
         */
        @Override
        public Message postProcessMessage(Message message) throws AmqpException {
            if (timeout != null) {
                message.getMessageProperties().setExpiration(timeout.toString());
            }
            return message;
        }

    }

}
