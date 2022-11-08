package cn.cyberpeace.mp.cscene.report.rabbitmq;

import cn.cyberpeace.mp.base.message.SnakeJsonMessageConverter;
import cn.cyberpeace.mp.base.message.rabbitmq.BaseSimpleMessageSender;
import cn.cyberpeace.mp.base.message.rabbitmq.SimpleMessageConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatusChangeRabbitmqSender extends BaseSimpleMessageSender {
    public static final String CSCENE_EXCHANGE_NAME = SimpleMessageConfig.GROUP_NAME + ".cscene";
    public static final String CSCENE_ROUTING_KEY = SimpleMessageConfig.GROUP_NAME + ".status_change.*";
    public static final String CSCENE_QUEUE_NAME = SimpleMessageConfig.GROUP_NAME + ".cscene_report.default";

    @Autowired
    public StatusChangeRabbitmqSender(AmqpTemplate amqpTemplate, SnakeJsonMessageConverter messageConverter) {
        super(amqpTemplate, messageConverter,
                new SimpleMessageConfig(CSCENE_EXCHANGE_NAME, CSCENE_ROUTING_KEY, CSCENE_QUEUE_NAME));
    }
}
