package cn.cyberpeace.mp.sceneplugincollection.config;

import cn.cyberpeace.mp.sceneplugincollection.util.CollectionCommonConfigUtil;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class WazuhProbeConfig {
    private static DefaultProbe defaultProbe;

    private static volatile boolean isInit = false;

    public static synchronized void init() {
        if (isInit) {
            return;
        }
        defaultProbe = JSON.parseObject(CollectionCommonConfigUtil.getConfigString(CollectionCommonConfigUtil.DEFAULT_CUSTOM_PROBES), DefaultProbe.class);
        isInit = true;
    }

    public DefaultProbe getDefaultProbe() {
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
