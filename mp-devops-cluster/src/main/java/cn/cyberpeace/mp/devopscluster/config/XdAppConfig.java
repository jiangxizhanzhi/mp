package cn.cyberpeace.mp.devopscluster.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "mp.xdapp")
@Data
public class XdAppConfig {

    @Value("${mp.xdapp.xd-one-server:http://192.168.140.4:10060}")
    private String xdOneServer;
    @Value("${mp.xdapp.xd-two-server:https://192.168.140.15}")
    private String xdTwoServer;
    @Value("${mp.xdapp.xd-two-access-key:5d83a38630884f6eadb659144695dbcb}")
    private String accessKey;
    @Value("${mp.xdapp.xd-two-secret-key:ea51d2c0760f4e82a0a7e2bc4d94199f}")
    private String secretKey;



}
