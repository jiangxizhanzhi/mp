package cn.cyberpeace.mp.base.message.rabbitmq;

import cn.cyberpeace.mp.base.util.ModuleUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.amqp.core.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleMessageConfig {

    /**
     * 分组前缀名称
     */
    public static final String GROUP_NAME = ModuleUtils.GROUP_NAME;

    /**
     * 默认mp rabbitmq交换器名称
     */
    public static final String DEFAULT_MP_EXCHANGE = GROUP_NAME + ".default";

    /**
     * 默认mq路由key
     */
    public static final String DEFAULT_MP_ROUTING_KEY = DEFAULT_MP_EXCHANGE;

    /**
     * 默认mq队列名称
     */
    public static final String DEFAULT_MP_QUEUE = DEFAULT_MP_EXCHANGE;

    private String exchangeName = DEFAULT_MP_EXCHANGE;

    private String routingKey = DEFAULT_MP_ROUTING_KEY;

    private String queueName = DEFAULT_MP_QUEUE;

    /**
     * 直连交换器
     * @return
     */
    public DirectExchange directExchange() {
        return new DirectExchange(getExchangeName());
    }

    /**
     * 主题交换器
     * @return
     */
    public TopicExchange topicExchange() {
        return new TopicExchange(getExchangeName());
    }

    /**
     * 扇形广播交换器
     * @return
     */
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(getExchangeName());
    }

    /**
     * 队列
     * @return
     */
    public Queue queue() {
        return new Queue(getQueueName());
    }

    /**
     * 直连交换器绑定路由key
     * @return
     */
    public Binding directBinding() {
        return BindingBuilder.bind(queue()).to(directExchange()).with(getRoutingKey());
    }

    /**
     * 主题交换器绑定路由key
     * @return
     */
    public Binding topicBinding() {
        return BindingBuilder.bind(queue()).to(topicExchange()).with(getRoutingKey());
    }

    /**
     * 广播交换器绑定路由key
     * @return
     */
    public Binding fanoutBinding() {
        return BindingBuilder.bind(queue()).to(fanoutExchange());
    }
}
