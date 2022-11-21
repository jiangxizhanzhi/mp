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

@Component
public class AuthStatusRabbitmqSender extends BaseSimpleMessageSender {

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    public AuthStatusRabbitmqSender(AmqpTemplate amqpTemplate, SnakeJsonMessageConverter messageConverter) {
        super(amqpTemplate, messageConverter, new SimpleMessageConfig(AuthConstant.AUTH_EXCHANGE_NAME, null, null));
    }

    @Bean
    public TopicExchange authTopicExchange() {
        return getMessageConfig().topicExchange();
    }

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
