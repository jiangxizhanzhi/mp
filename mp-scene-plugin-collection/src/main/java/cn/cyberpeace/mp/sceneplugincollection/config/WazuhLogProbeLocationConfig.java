package cn.cyberpeace.mp.sceneplugincollection.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 日志采集地址配置类
 * @Author： hewei
 * @Date： 2022.8.3 18:57
 */
@Component
@ConfigurationProperties(prefix = "mp.collection.location")
@Slf4j
@Data
public class WazuhLogProbeLocationConfig {
    private List<ProbeLocation> locations;

    @Data
    public static class ProbeLocation {
        private String probeId;
        private String logFormat;
        private List<String> location;
        private String osType;
    }
}
