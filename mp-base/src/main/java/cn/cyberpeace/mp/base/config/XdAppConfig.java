package cn.cyberpeace.mp.base.config;

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
    @Value("${mp.xdapp.xd-two-access-key:e2a21753c0904c299512408b8ea97372}")
    private String accessKey;
    @Value("${mp.xdapp.xd-two-secret-key:769ce2d564fa4399b1ade854daccde19}")
    private String secretKey;
    @Value("${mp.xdapp.xd-two-device-id}")
    private String xdTwoDeviceId;
    @Value("${mp.xdapp.xd-two-port-id}")
    private String xdTwoPortId;
    @Value("${mp.xdapp.xd-three-server:http://192.168.140.20:5701}")
    private String xdThreeServer;



}
