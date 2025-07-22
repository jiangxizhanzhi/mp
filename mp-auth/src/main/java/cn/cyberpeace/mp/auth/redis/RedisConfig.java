package cn.cyberpeace.mp.auth.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @Description: redis配置
 */
@Slf4j
@EnableAsync
@Configuration
public class RedisConfig {

    @Value("${spring.redis.topic}")
    private String redisTopic;

    @Value("${spring.redis.threadName}")
    private String redisThreadName;

    /**
     * redis配置
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("===============redis：初始化redisTemplate开始");
        // 实例化redisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 设置链接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // key采用String的序列化
        redisTemplate.setKeySerializer(keySerializer());
        // value采用jackson序列化
        redisTemplate.setValueSerializer(valueSerializer());
        // Hash key采用String的序列化
        redisTemplate.setHashKeySerializer(keySerializer());
        // Hash value采用jackson序列化
        redisTemplate.setHashValueSerializer(valueSerializer());
        log.info("===============redis：key和value采用jackson序列化成功");
        // 执行函数，初始化RedisTemplate
        redisTemplate.afterPropertiesSet();
        log.info("===============redis：初始化redisTemplate结束");
        return redisTemplate;
    }



    @Bean
    public RedisMessageListenerContainer getRedisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory, RedisMessageListener redisMessageListener) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        // 设置连接工厂
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        // 绑定监听器和信道
        bindChannelTopicList(redisMessageListenerContainer, redisMessageListener);
        log.info("===============redis：绑定自定义监听器和信道成功，主题【{}】", redisTopic);
        // 配置任务执行器
        redisMessageListenerContainer.setTaskExecutor(taskExecutor());
        log.info("===============redis：配置自定义任务执行器成功，线程前缀【{}】", redisThreadName);
        return redisMessageListenerContainer;
    }

    /**
     * 定义redis主题（信道）
     * @return
     */
    public void bindChannelTopicList(RedisMessageListenerContainer redisMessageListenerContainer, RedisMessageListener redisMessageListener) {
        List<String> channelTopicList = Arrays.asList(redisTopic.split(","));
        for (String channelTopic : channelTopicList) {
            redisMessageListenerContainer.addMessageListener(redisMessageListener, new ChannelTopic(channelTopic));
        }
    }

    /**
     * 注册任务执行器
     * @return
     */
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(5);
        // 设置最大线程数
        executor.setMaxPoolSize(10);
        // 设置任务队列容量
        executor.setQueueCapacity(10);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称（线程前缀名称，有助于区分不同线程池之间的线程比如：taskExecutor-）
        executor.setThreadNamePrefix(redisThreadName);
        // 设置允许核心线程超时
        executor.setAllowCoreThreadTimeOut(true);
        return executor;
    }

    /**
     * key类型采用String序列化
     * @return
     */
    private RedisSerializer<String> keySerializer() {
        return  new StringRedisSerializer();
    }

    /**
     * value采用JSON序列化
     * @return
     */
    private RedisSerializer<Object> valueSerializer() {
        // 设置jackson序列化
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        // 设置序列化对象
        jackson2JsonRedisSerializer.setObjectMapper(getMapper());
        return jackson2JsonRedisSerializer;
    }

    /**
     * 使用com.fasterxml.jackson.databind.ObjectMapper
     * 对数据进行处理包括java8里的时间
     * @return
     */
    private ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 设置可见性
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 默认键入对象
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        // 设置Java 8 时间序列化
        JavaTimeModule timeModule = new JavaTimeModule();
        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        timeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        timeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        timeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        // 禁用把时间转为时间戳
        mapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        mapper.registerModule(timeModule);
        return mapper;
    }
}


