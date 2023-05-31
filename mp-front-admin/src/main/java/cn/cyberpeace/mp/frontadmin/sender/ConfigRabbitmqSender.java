package cn.cyberpeace.mp.frontadmin.sender;

import cn.cyberpeace.mp.base.message.FormatMessage;
import cn.cyberpeace.mp.base.message.SnakeJsonMessageConverter;
import cn.cyberpeace.mp.base.message.rabbitmq.BaseSimpleMessageSender;
import cn.cyberpeace.mp.base.message.rabbitmq.SimpleMessageConfig;
import cn.cyberpeace.mp.frontadmin.constant.SysConfigConstant;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Description: 系统配置变动消息发送器
 * @Author zhouxinghuai
 * @Date：2023/05/29 11:13
 */
@Component
public class ConfigRabbitmqSender extends BaseSimpleMessageSender {

    @Autowired
    public ConfigRabbitmqSender(AmqpTemplate amqpTemplate, SnakeJsonMessageConverter messageConverter) {
        super(amqpTemplate, messageConverter, new SimpleMessageConfig(SysConfigConstant.SYS_CONFIG_EXCHANGE_NAME, null, null));
    }

    @Bean
    public TopicExchange sysConfigTopicExchange() {
        return getMessageConfig().topicExchange();
    }

    public <T> void send(T data, String type, String routingKey, Long timeout) {
        FormatMessage<T> formatMessage = new FormatMessage<>(data, type, null);
        formatMessage.setContext(null);

        BaseSimpleMessagePostProcessor messagePostProcessor = new BaseSimpleMessagePostProcessor();
        if (timeout != null) {
            messagePostProcessor.setTimeout(timeout);
        } else {
            messagePostProcessor.setTimeout(3600000L);
        }
        getAmqpTemplate().convertAndSend(getMessageConfig().getExchangeName(), routingKey,
                getMessageConverter().toMessageString(formatMessage), messagePostProcessor);
    }
}
