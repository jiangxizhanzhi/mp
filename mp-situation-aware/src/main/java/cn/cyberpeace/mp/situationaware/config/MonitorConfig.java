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
@Data
@Configuration
@ConfigurationProperties(prefix = "mp.devops.monitor", ignoreInvalidFields = true)
public class MonitorConfig {
    private String cpCloudInterval;

    private String businessInterval;

    private List<String> cpCloudServerList;

    private List<String> businessServerList;

    private List<String> cpCloudRestartRestrict;

    private List<String> businessRestartRestrict;
}
