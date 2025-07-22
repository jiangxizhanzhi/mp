package cn.cyberpeace.mp.auth.redis;


import cn.cyberpeace.mp.auth.service.UserAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: redis消费服务类
 */
@Slf4j
@Service
public class RedisConsumerService {

    @Autowired
    private UserAsyncService userAsyncService;

    /**
     * 根据channel再继续映射到不同的业务实现
     * @param channel 主题信道
     * @param message 消息
     */
    public void processMessage(String channel, String message) throws Exception {
        log.info("【{}】接收的结果：{}", channel, message);
        // TODO 根据channel映射到不同的业务实现
        if("uia-topic-addUser".equals(channel)) {
            userAsyncService.addUser(message);
        }else if("uia-topic-updateUser".equals(channel)){
            userAsyncService.updateUser(message);
        } else if ("uia-topic-deleteUser".equals(channel)){
            userAsyncService.deleteUser(message);
        }
    }
}


