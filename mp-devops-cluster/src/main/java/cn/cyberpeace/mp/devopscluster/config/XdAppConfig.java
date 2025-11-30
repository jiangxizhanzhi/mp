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

}
