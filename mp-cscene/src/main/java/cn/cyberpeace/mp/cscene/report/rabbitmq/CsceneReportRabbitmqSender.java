package cn.cyberpeace.mp.cscene.report.rabbitmq;

import cn.cyberpeace.mp.base.message.SnakeJsonMessageConverter;
import cn.cyberpeace.mp.base.message.rabbitmq.BaseSimpleMessageSender;
import cn.cyberpeace.mp.base.message.rabbitmq.SimpleMessageConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CsceneReportRabbitmqSender extends BaseSimpleMessageSender {
    public static final String CSCENE_EXCHANGE_NAME = SimpleMessageConfig.GROUP_NAME + ".cscene";
    public static final String CSCENE_ROUTING_KEY = CSCENE_EXCHANGE_NAME;
    public static final String CSCENE_QUEUE_NAME = CSCENE_ROUTING_KEY;

    @Autowired
    public CsceneReportRabbitmqSender(AmqpTemplate amqpTemplate, SnakeJsonMessageConverter messageConverter) {
        super(amqpTemplate, messageConverter,
                new SimpleMessageConfig(CSCENE_EXCHANGE_NAME, CSCENE_ROUTING_KEY, CSCENE_QUEUE_NAME));
    }

    @Bean
    public Queue csceneQueue() {
        return getMessageConfig().queue();
    }

    @Bean
    public Binding csceneBinding() {
        return getMessageConfig().directBinding();
    }

    @Bean
    public DirectExchange csceneExchange() {
        return getMessageConfig().directExchange();
    }
}
