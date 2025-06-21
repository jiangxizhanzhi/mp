 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Application.java.
 // File size 3845.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.constant.BaseSingleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import springfox.boot.starter.autoconfigure.SpringfoxConfigurationProperties;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName Application
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@EnableAsync
@EnableScheduling
@EnableCaching
@EnableWebSocket
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class} )
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            String localHost = "localhost";
            ServerProperties serverProperties = BaseSingleton.getBaseAppConfig().getServerProperties();
            String protocol = (serverProperties.getSsl() != null && serverProperties.getSsl().isEnabled()) ? "https" : "http";
            String hostAddress = serverProperties.getAddress() == null ? InetAddress.getLocalHost().getHostAddress() : serverProperties.getAddress().getHostAddress();
            Integer port= serverProperties.getPort();
            String contextPath = serverProperties.getServlet().getContextPath();
            contextPath = StringUtils.isEmpty(contextPath) ? StringUtils.EMPTY : contextPath;
            String localServer = String.format("%s://%s:%d%s", protocol, localHost, port, contextPath);
            String server = String.format("%s://%s:%d%s", protocol, hostAddress, port, contextPath);
            List<String> serverLines = new ArrayList<>() {{
                add(StringUtils.EMPTY);
                add("----------------------------------------------------------");
                add(String.format("    [LOCAL]    Access Url: %s", localServer));
                add(String.format("    [EXTERNAL] Access Url: %s", server));
            }};

            SpringfoxConfigurationProperties springfoxConfigurationProperties = null;
            try {
                springfoxConfigurationProperties = BeanCommon.getBean(SpringfoxConfigurationProperties.class);
            } catch (Exception e) {
                log.debug("springfox config load failed");
            }
            if (springfoxConfigurationProperties != null && springfoxConfigurationProperties.isEnabled()) {
                serverLines.add(String.format("    [LOCAL]    Swagger Doc: %s/doc.html", localServer));
                serverLines.add(String.format("    [EXTERNAL] Swagger Doc: %s/doc.html", server));
            }

            serverLines.add("----------------------------------------------------------");
            serverLines.add(StringUtils.EMPTY);
            log.info(StringUtils.joinWith("\n", serverLines.toArray()));
        };
    }
}
