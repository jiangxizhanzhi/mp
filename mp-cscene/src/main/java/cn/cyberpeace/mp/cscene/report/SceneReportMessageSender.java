package cn.cyberpeace.mp.cscene.report;

import cn.cyberpeace.mp.base.message.JsonMessageConverter;
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
public class SceneReportMessageSender extends BaseSimpleMessageSender {
    public static final String CSCENE_EXCHANGE_NAME = SimpleMessageConfig.GROUP_NAME + ".cscene";
    public static final String CSCENE_QUEUE_NAME = SimpleMessageConfig.GROUP_NAME + ".cscene_scene_report";

    @Autowired
    public SceneReportMessageSender(AmqpTemplate amqpTemplate, SnakeJsonMessageConverter messageConverter) {
        super(amqpTemplate, messageConverter,
                new SimpleMessageConfig(CSCENE_EXCHANGE_NAME, CSCENE_QUEUE_NAME, CSCENE_QUEUE_NAME));
    }

    @Bean
    public Queue csceneSceneReportQueue() {
        return getMessageConfig().queue();
    }

    @Bean
    public Binding csceneSceneReportBinding() {
        return getMessageConfig().directBinding();
    }

    @Bean
    public DirectExchange csceneSceneReportExchange() {
        return getMessageConfig().directExchange();
    }
}
