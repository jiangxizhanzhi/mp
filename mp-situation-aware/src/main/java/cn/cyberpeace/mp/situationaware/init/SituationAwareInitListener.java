package cn.cyberpeace.mp.situationaware.init;

import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.service.NetworkTrafficGroupService;
import cn.cyberpeace.mp.situationaware.service.wazuh.SecurityService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Order(1)
@Component
@Slf4j
public class SituationAwareInitListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private NetworkTrafficGroupService networkTrafficGroupService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("prepare init actions for mp system");

        
        SituationalAwareConstant.wazuhToken = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(14, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public String load(String key) {
                        String token = securityService.authenticate();
                        return token;
                    }
                });

        networkTrafficGroupService.initArkimeNetwork();
    }


}
