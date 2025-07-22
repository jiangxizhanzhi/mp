package cn.cyberpeace.mp.auth.redis;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

/**
 * @Description: redis自定义监听器
 */
@Slf4j
@Component
public class RedisMessageListener implements MessageListener {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisConsumerService consumeService;

    /**
     * 消息处理
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(pattern);
        log.info("onMessage --> 消息通道是：{}", channel);
        try {
            RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
            Object deserializer = valueSerializer.deserialize(message.getBody());
            log.info("【{}】反序列化的结果：{}", channel, deserializer);
            if(deserializer != null) {
                // redis消息进行处理
                consumeService.processMessage(channel, deserializer.toString());
                log.info("【{}】处理redis消息完成", channel);
            }else {
                log.info("【{}】消息对象为空", channel);
            }
        } catch (Exception e){
            log.error("【{}】处理redis消息异常", channel, e);
        }
    }
}


