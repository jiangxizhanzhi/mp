 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name StatusChangeRabbitmqSender.java.
 // File size 1401.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.report.rabbitmq;

import cn.cyberpeace.mp.base.message.SnakeJsonMessageConverter;
import cn.cyberpeace.mp.base.message.rabbitmq.BaseSimpleMessageSender;
import cn.cyberpeace.mp.base.message.rabbitmq.SimpleMessageConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName StatusChangeRabbitmqSender
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSimpleMessageSender
 */
@Component
public class StatusChangeRabbitmqSender extends BaseSimpleMessageSender {
    public static final String CSCENE_EXCHANGE_NAME = SimpleMessageConfig.GROUP_NAME + ".cscene";
    public static final String CSCENE_ROUTING_KEY = SimpleMessageConfig.GROUP_NAME + ".status_change.*";
    public static final String CSCENE_QUEUE_NAME = SimpleMessageConfig.GROUP_NAME + ".cscene_report.default";

    /**
     * StatusChangeRabbitmqSender
     *
     * @param amqpTemplate
     * @param messageConverter
     */
    @Autowired
    public StatusChangeRabbitmqSender(AmqpTemplate amqpTemplate, SnakeJsonMessageConverter messageConverter) {
        super(amqpTemplate, messageConverter,
                new SimpleMessageConfig(CSCENE_EXCHANGE_NAME, CSCENE_ROUTING_KEY, CSCENE_QUEUE_NAME));
    }
}
