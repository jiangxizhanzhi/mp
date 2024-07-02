package cn.cyberpeace.mp.cscene.report.rabbitmq;

import cn.cyberpeace.mp.base.message.SnakeJsonMessageConverter;
import cn.cyberpeace.mp.base.message.rabbitmq.BaseSimpleMessageSender;
import cn.cyberpeace.mp.base.message.rabbitmq.SimpleMessageConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CsceneSceneReportRabbitmqSender extends BaseSimpleMessageSender {
    public static final String CSCENE_SCENE_REPORT_EXCHANGE_NAME = CsceneReportRabbitmqSender.CSCENE_EXCHANGE_NAME;
    public static final String CSCENE_SCENE_REPORT_ROUTING_KEY = SimpleMessageConfig.GROUP_NAME + ".cscene_scene_report.*";
    public static final String CSCENE_SCENE_REPORT_QUEUE_NAME = SimpleMessageConfig.GROUP_NAME + ".cscene_scene_report.default";

    /**
     * CsceneSceneReportRabbitmqSender
     *
     * @param amqpTemplate
     * @param messageConverter
     */
    @Autowired
    public CsceneSceneReportRabbitmqSender(AmqpTemplate amqpTemplate, SnakeJsonMessageConverter messageConverter) {
        super(amqpTemplate, messageConverter,
                new SimpleMessageConfig(CSCENE_SCENE_REPORT_EXCHANGE_NAME,
                        CSCENE_SCENE_REPORT_ROUTING_KEY, CSCENE_SCENE_REPORT_QUEUE_NAME));
    }

    /**
     * csceneSceneReportQueue
     *
     * @return
     *///    @Bean
    public Queue csceneSceneReportQueue() {
        return getMessageConfig().queue();
    }

    /**
     * csceneSceneReportBinding
     *
     * @return
     *///    @Bean
    public Binding csceneSceneReportBinding() {
        return getMessageConfig().topicBinding();
    }

    /**
     * csceneSceneReportExchange
     *
     * @return
     */
    @Bean
    public TopicExchange csceneSceneReportExchange() {
        return getMessageConfig().topicExchange();
    }
}
