package cn.cyberpeace.mp.situationaware.listener;

import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.service.wazuh.WazuhClient;
import cn.cyberpeace.mp.situationaware.service.wazuh.WazuhClientProxy;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 启动完后生命周期进行
 */
@Order(1)
@Component
@Slf4j
public class SituationAwareInitListener implements ApplicationListener<ApplicationReadyEvent> {

    @Resource
    private WazuhClient wazuhClient;

    @Value("${mp.situationaware.wazuh.username}")
    private String wazuhUsername;

    @Value("${mp.situationaware.wazuh.password}")
    private String wazuhPassword;



    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        WazuhClientProxy.init(wazuhUsername, wazuhPassword);

        //定时更新token初始化
        SituationalAwareConstant.wazuhToken = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(14, TimeUnit.MINUTES)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) {
                        String token = wazuhClient.authenticate();
                        return token;
                    }
                });
    }


}
