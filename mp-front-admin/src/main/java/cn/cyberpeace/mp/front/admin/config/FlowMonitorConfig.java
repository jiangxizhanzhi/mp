package cn.cyberpeace.mp.front.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/19 13:52
 */
@Data
@Component
@ConfigurationProperties(prefix = "mp.flow-monitor")
public class FlowMonitorConfig {

    private Boolean enabled;

    private String frequency;

    private String initDelay;

    private String level;

    private Mail mail;

    private SysNotice sysNotice;


    @Data
    public static class Mail {

        private Boolean enabled;

        private String host;

        private int port;

        private String from;

        private String to;

        private String username;

        private String password;

        private String auth;

        private String defaultEncoding;

        private Integer timeout;

    }



    @Data
    public static class SysNotice {

        private Boolean enabled;

    }


    @PostConstruct
    public void init() {
        System.out.println(this);
    }


}
