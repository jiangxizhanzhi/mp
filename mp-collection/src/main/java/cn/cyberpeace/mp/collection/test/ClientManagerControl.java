package cn.cyberpeace.mp.collection.test;

import cn.cyberpeace.mp.collection.core.clientmanager.ClientManagerCommon;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ClientManagerControl {
    @Value("${mp.situationaware.wazuh.base-url}")
    private String wazuhUrl;

    @Value("${mp.situationaware.wazuh.username}")
    private String wazuhUsername;

    @Value("${mp.situationaware.wazuh.password}")
    private String wazuhPassword;

    @Value("${mp.situationaware.es.server}")
    private String esServer;

    @Value("${mp.situationaware.mgo.server}")
    private String mgoServer;

    public static ClientManagerCommon clientManagerCommon;

    @PostConstruct
    public void test() {
        clientManagerCommon = new ClientManagerCommon(wazuhUrl, wazuhUsername, wazuhPassword, esServer, mgoServer);
    }
}
