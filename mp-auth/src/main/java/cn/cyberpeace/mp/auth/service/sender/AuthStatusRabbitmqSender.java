 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AuthStatusRabbitmqSender.java.
 // File size 1892.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
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
  * 验证状态rabbitmq发送方
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Component
public class AuthStatusRabbitmqSender extends BaseSimpleMessageSender {

     /**
      * 身份验证配置
      */
     @Autowired
    private AuthConfig authConfig;

     /**
      * 验证状态rabbitmq发送方
      *
      * @param amqpTemplate     amqp模板
      * @param messageConverter 消息转换器
      */
     @Autowired
    public AuthStatusRabbitmqSender(AmqpTemplate amqpTemplate, SnakeJsonMessageConverter messageConverter) {
        super(amqpTemplate, messageConverter, new SimpleMessageConfig(AuthConstant.AUTH_EXCHANGE_NAME, null, null));
    }

     /**
      * 专题交流
      *
      * @return {@link TopicExchange }
      */
     @Bean
    public TopicExchange authTopicExchange() {
        return getMessageConfig().topicExchange();
    }

     /**
      * 发送
      *
      * @param data       数据
      * @param type       类型
      * @param routingKey
      * @param timeout    超时
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
