 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AuthStatusRabbitmqSender.java.
 // File size 2366.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.service.sender;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.message.FormatMessage;
import cn.cyberpeace.mp.base.message.SnakeJsonMessageConverter;
import cn.cyberpeace.mp.base.message.rabbitmq.BaseSimpleMessageSender;
import cn.cyberpeace.mp.base.message.rabbitmq.SimpleMessageConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @ClassName AuthStatusRabbitmqSender
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSimpleMessageSender
 */
@Component
public class AuthStatusRabbitmqSender extends BaseSimpleMessageSender {

    @Autowired
    private AuthConfig authConfig;

    /**
     * AuthStatusRabbitmqSender
     *
     * @param amqpTemplate
     * @param messageConverter
     */
    @Autowired
    public AuthStatusRabbitmqSender(AmqpTemplate amqpTemplate, SnakeJsonMessageConverter messageConverter) {
        super(amqpTemplate, messageConverter, new SimpleMessageConfig(AuthConstant.AUTH_EXCHANGE_NAME, null, null));
    }

    /**
     * authTopicExchange
     *
     * @return
     */
    @Bean
    public TopicExchange authTopicExchange() {
        return getMessageConfig().topicExchange();
    }

    /**
     * send
     *
     * @param data
     * @param type
     * @param routingKey
     * @param timeout
     */
    public <T> void send(T data, String type, String routingKey, Long timeout) {
        FormatMessage<T> formatMessage = new FormatMessage<>(data, type, null);
        formatMessage.setContext(null);

        BaseSimpleMessagePostProcessor messagePostProcessor = new BaseSimpleMessagePostProcessor();
        if (timeout != null) {
            messagePostProcessor.setTimeout(timeout);
        } else {
            messagePostProcessor.setTimeout(authConfig.getMqMsgTimeout());
        }
        getAmqpTemplate().convertAndSend(getMessageConfig().getExchangeName(), routingKey,
                getMessageConverter().toMessageString(formatMessage), messagePostProcessor);
    }
}
