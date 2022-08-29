package cn.cyberpeace.mp.collection.listener;

import cn.cyberpeace.mp.collection.core.es.RestHighLevelClientImpl;
import cn.cyberpeace.mp.collection.core.wazuh.WazuhClientProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 启动完后生命周期进行
 */
@Order(1)
@Component
@Slf4j
public class CollectionInitListener implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${mp.situationaware.wazuh.username}")
    private String wazuhUsername;

    @Value("${mp.situationaware.wazuh.password}")
    private String wazuhPassword;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        RestHighLevelClientImpl.initClientMap();
        WazuhClientProxy.init(wazuhUsername, wazuhPassword);
    }

}
