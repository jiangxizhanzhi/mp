package cn.cyberpeace.mp.sceneplugincollection.config;

import cn.cyberpeace.mp.sceneplugincollection.service.CollectionCommonConfigService;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 默认自定义探针配置类
 * @Author： hewei
 * @Date： 2022.3.23 15:49
 */
@Component
public class WazuhProbeConfig {
    private static DefaultProbe defaultProbe;

    private static volatile boolean isInit = false;

    public static synchronized void init() {
        if (isInit) {
            return;
        }
        defaultProbe = JSON.parseObject(CollectionCommonConfigService.getConfigString(CollectionCommonConfigService.DEFAULT_CUSTOM_PROBES).getSource(), DefaultProbe.class);
        isInit = true;
    }

    public DefaultProbe getDefaultProbe() {
        if (!isInit) {
            init();
        }
        return defaultProbe;
    }

    @Data
    public static class DefaultProbe {
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
}
