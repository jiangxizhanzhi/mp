package cn.cyberpeace.mp.base.message.rabbitmq;

import cn.cyberpeace.mp.base.util.ModuleUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.amqp.core.*;

/**
 * @ClassName SimpleMessageConfig
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleMessageConfig {

    public static final String GROUP_NAME = ModuleUtils.GROUP_NAME;

    public static final String DEFAULT_MP_EXCHANGE = GROUP_NAME + ".default";

    public static final String DEFAULT_MP_ROUTING_KEY = DEFAULT_MP_EXCHANGE;

    public static final String DEFAULT_MP_QUEUE = DEFAULT_MP_EXCHANGE;

    private String exchangeName = DEFAULT_MP_EXCHANGE;

    private String routingKey = DEFAULT_MP_ROUTING_KEY;

    private String queueName = DEFAULT_MP_QUEUE;

    /**
     * directExchange
     *
     * @return
     */
    public DirectExchange directExchange() {
        return new DirectExchange(getExchangeName());
    }

    /**
     * topicExchange
     *
     * @return
     */
    public TopicExchange topicExchange() {
        return new TopicExchange(getExchangeName());
    }

    /**
     * fanoutExchange
     *
     * @return
     */
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(getExchangeName());
    }

    /**
     * queue
     *
     * @return
     */
    public Queue queue() {
        return new Queue(getQueueName());
    }

    /**
     * directBinding
     *
     * @return
     */
    public Binding directBinding() {
        return BindingBuilder.bind(queue()).to(directExchange()).with(getRoutingKey());
    }

    /**
     * topicBinding
     *
     * @return
     */
    public Binding topicBinding() {
        return BindingBuilder.bind(queue()).to(topicExchange()).with(getRoutingKey());
    }

    /**
     * fanoutBinding
     *
     * @return
     */
    public Binding fanoutBinding() {
        return BindingBuilder.bind(queue()).to(fanoutExchange());
    }
}
