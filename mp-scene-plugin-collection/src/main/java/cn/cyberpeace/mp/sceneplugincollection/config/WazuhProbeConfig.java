package cn.cyberpeace.mp.sceneplugincollection.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 自定义探针配置类
 * @Author： hewei
 * @Date： 2022.3.23 15:49
 */
@Component
@ConfigurationProperties(prefix = "mp.collection")
@Slf4j
@Data
public class WazuhProbeConfig {
    private String defaultInterval;
    private List<NewProbe> probes;

    @Data
    public static class NewProbe {
        private String probeName;
        private String id;
        private String interval;
        private String outputFormat;
    }
}
