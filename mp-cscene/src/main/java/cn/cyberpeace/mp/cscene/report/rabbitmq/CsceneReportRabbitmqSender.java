package cn.cyberpeace.mp.cscene.report.rabbitmq;

import cn.cyberpeace.mp.base.message.SnakeJsonMessageConverter;
import cn.cyberpeace.mp.base.message.rabbitmq.BaseSimpleMessageSender;
import cn.cyberpeace.mp.base.message.rabbitmq.SimpleMessageConfig;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CsceneReportRabbitmqSender extends BaseSimpleMessageSender {
    public static final String CSCENE_EXCHANGE_NAME = SimpleMessageConfig.GROUP_NAME + ".cscene";
    public static final String CSCENE_ROUTING_KEY = SimpleMessageConfig.GROUP_NAME + ".cscene_report.*";
    public static final String CSCENE_QUEUE_NAME = SimpleMessageConfig.GROUP_NAME + ".cscene_report.default";

    @Autowired
    public CsceneReportRabbitmqSender(AmqpTemplate amqpTemplate, SnakeJsonMessageConverter messageConverter) {
        super(amqpTemplate, messageConverter,
                new SimpleMessageConfig(CSCENE_EXCHANGE_NAME, CSCENE_ROUTING_KEY, CSCENE_QUEUE_NAME));
    }

//    @Bean
    public Queue csceneQueue() {
        return getMessageConfig().queue();
    }

//    @Bean
    public Binding csceneBinding() {
        return getMessageConfig().topicBinding();
    }

    @Bean
    public TopicExchange csceneExchange() {
        return getMessageConfig().topicExchange();
    }
}
