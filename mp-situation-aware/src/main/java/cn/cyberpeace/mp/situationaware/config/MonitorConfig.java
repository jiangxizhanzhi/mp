package cn.cyberpeace.mp.situationaware.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/14 14:58
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "mp.devops.monitor",ignoreInvalidFields = true)
@Data
public class MonitorConfig {

    // 云平台服务监控
    private List<MonitorProbe> cpSrvMonitors;

    // 业务服务监控
    private List<MonitorProbe> bizSrvMonitors;

    @Data
    public static class MonitorProbe {

        private String probeName;

        private String launchId;

        private String interval;

        private String serviceList;
    }

}
