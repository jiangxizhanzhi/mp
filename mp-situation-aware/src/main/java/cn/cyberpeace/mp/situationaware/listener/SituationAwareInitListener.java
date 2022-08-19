package cn.cyberpeace.mp.situationaware.listener;

import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.service.EsClientProxy;
import cn.cyberpeace.mp.situationaware.service.wazuh.WazuhClient;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import lombok.extern.slf4j.Slf4j;
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


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("prepare init actions for mp system");

        EsClientProxy.init();

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
